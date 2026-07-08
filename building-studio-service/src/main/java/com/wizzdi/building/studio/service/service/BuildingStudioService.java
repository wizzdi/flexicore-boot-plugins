package com.wizzdi.building.studio.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.building.studio.model.*;
import com.wizzdi.building.studio.service.data.BuildingStudioRepository;
import com.wizzdi.building.studio.service.request.*;
import com.wizzdi.building.studio.service.response.BuildingStudioConversionResponse;
import com.wizzdi.building.studio.service.response.BuildingStudioProcessResult;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.file.request.FileResourceCreate;
import com.wizzdi.flexicore.file.service.FileResourceService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Extension
@Component
public class BuildingStudioService implements Plugin {

    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_UPLOADED = "UPLOADED";
    public static final String STATUS_CONVERTING = "CONVERTING";
    public static final String STATUS_READY = "READY";
    public static final String STATUS_FAILED = "FAILED";

    @Autowired
    private BuildingStudioRepository repository;
    @Autowired
    private BasicService basicService;
    @Autowired
    private FileResourceService fileResourceService;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${building.studio.workDir:/home/flexicore/building-studio}")
    private String workDir;

    @Value("${building.studio.pythonExecutable:python3}")
    private String pythonExecutable;

    @Value("${building.studio.runnerPath:/home/flexicore/building-studio/building_studio_cli.py}")
    private String runnerPath;

    @Value("${building.studio.odaExecutable:}")
    private String odaExecutable;

    @Value("${building.studio.conversionTimeoutSeconds:900}")
    private long conversionTimeoutSeconds;

    public PaginationResponse<BuildingBundle> getAllBuildingBundles(BuildingBundleFilter filter, SecurityContext securityContext) {
        List<BuildingBundle> list = repository.listAllBuildingBundles(filter, securityContext);
        long count = repository.countAllBuildingBundles(filter, securityContext);
        return new PaginationResponse<>(list, filter, count);
    }

    public void validate(BuildingBundleFilter filter, SecurityContext securityContext) {
        if (filter.getBasicPropertiesFilter() != null) {
            basicService.validate(filter, securityContext);
        }
    }

    public BuildingBundle createBuildingBundle(BuildingBundleCreate request, SecurityContext securityContext) {
        BuildingBundle bundle = createBuildingBundleNoMerge(request, securityContext);
        repository.merge(bundle);
        return bundle;
    }

    public BuildingBundle createBuildingBundleNoMerge(BuildingBundleCreate request, SecurityContext securityContext) {
        BuildingBundle bundle = new BuildingBundle();
        bundle.setId(UUID.randomUUID().toString());
        updateBuildingBundleNoMerge(bundle, request);
        BaseclassService.createSecurityObjectNoMerge(bundle, securityContext);
        return bundle;
    }

    public boolean updateBuildingBundleNoMerge(BuildingBundle bundle, BuildingBundleCreate request) {
        boolean updated = basicService.updateBasicNoMerge(request, bundle);
        if (request.getExternalId() != null && !Objects.equals(bundle.getExternalId(), request.getExternalId())) { bundle.setExternalId(request.getExternalId()); updated = true; }
        if (request.getStatus() != null && !Objects.equals(bundle.getStatus(), request.getStatus())) { bundle.setStatus(request.getStatus()); updated = true; }
        if (request.getSourceDwg() != null && !Objects.equals(bundle.getSourceDwg(), request.getSourceDwg())) { bundle.setSourceDwg(request.getSourceDwg()); updated = true; }
        if (request.getActiveSvg() != null && !Objects.equals(bundle.getActiveSvg(), request.getActiveSvg())) { bundle.setActiveSvg(request.getActiveSvg()); updated = true; }
        if (request.getActiveGlb() != null && !Objects.equals(bundle.getActiveGlb(), request.getActiveGlb())) { bundle.setActiveGlb(request.getActiveGlb()); updated = true; }
        return updated;
    }

    public BuildingBundle updateBuildingBundle(BuildingBundleUpdate request, SecurityContext securityContext) {
        BuildingBundle bundle = request.getBuildingBundle();
        if (updateBuildingBundleNoMerge(bundle, request)) {
            repository.merge(bundle);
        }
        return bundle;
    }

    @Transactional
    public BuildingDwg registerDwg(BuildingDwgCreate request, SecurityContext securityContext) {
        BuildingDwg dwg = new BuildingDwg();
        dwg.setId(UUID.randomUUID().toString());
        updateDwgNoMerge(dwg, request);
        dwg.setConversionStatus(STATUS_UPLOADED);
        BaseclassService.createSecurityObjectNoMerge(dwg, securityContext);
        BuildingBundle bundle = request.getBuildingBundle();
        if (bundle != null) {
            bundle.setSourceDwg(dwg).setStatus(STATUS_UPLOADED);
        }
        BuildingBundleFile file = createBundleFileNoMerge(bundle, request.getFileResource(), "DWG", BuildingDwg.class.getCanonicalName(), dwg.getId(), true, request.getExternalId(), securityContext);
        mergeNonNull(dwg, bundle, file);
        return dwg;
    }

    public boolean updateDwgNoMerge(BuildingDwg dwg, BuildingDwgCreate request) {
        boolean updated = basicService.updateBasicNoMerge(request, dwg);
        if (request.getExternalId() != null && !Objects.equals(dwg.getExternalId(), request.getExternalId())) { dwg.setExternalId(request.getExternalId()); updated = true; }
        if (request.getBuildingBundle() != null && !Objects.equals(dwg.getBuildingBundle(), request.getBuildingBundle())) { dwg.setBuildingBundle(request.getBuildingBundle()); updated = true; }
        if (request.getFileResource() != null && !Objects.equals(dwg.getFileResource(), request.getFileResource())) { dwg.setFileResource(request.getFileResource()); updated = true; }
        if (request.getDxfFileResource() != null && !Objects.equals(dwg.getDxfFileResource(), request.getDxfFileResource())) { dwg.setDxfFileResource(request.getDxfFileResource()); updated = true; }
        if (request.getOriginalFileName() != null && !Objects.equals(dwg.getOriginalFileName(), request.getOriginalFileName())) { dwg.setOriginalFileName(request.getOriginalFileName()); updated = true; }
        return updated;
    }

    @Transactional
    public BuildingSvg registerSvg(BuildingSvgCreate request, SecurityContext securityContext) {
        BuildingSvg svg = new BuildingSvg();
        svg.setId(UUID.randomUUID().toString());
        updateSvgNoMerge(svg, request);
        BaseclassService.createSecurityObjectNoMerge(svg, securityContext);
        BuildingBundle bundle = request.getBuildingBundle();
        if (bundle != null) {
            bundle.setActiveSvg(svg).setStatus(STATUS_READY);
        }
        BuildingBundleFile file = createBundleFileNoMerge(bundle, request.getFileResource(), "SVG", BuildingSvg.class.getCanonicalName(), svg.getId(), true, request.getExternalId(), securityContext);
        mergeNonNull(svg, bundle, file);
        return svg;
    }

    public boolean updateSvgNoMerge(BuildingSvg svg, BuildingSvgCreate request) {
        boolean updated = basicService.updateBasicNoMerge(request, svg);
        if (request.getExternalId() != null && !Objects.equals(svg.getExternalId(), request.getExternalId())) { svg.setExternalId(request.getExternalId()); updated = true; }
        if (request.getBuildingBundle() != null && !Objects.equals(svg.getBuildingBundle(), request.getBuildingBundle())) { svg.setBuildingBundle(request.getBuildingBundle()); updated = true; }
        if (request.getSourceDwg() != null && !Objects.equals(svg.getSourceDwg(), request.getSourceDwg())) { svg.setSourceDwg(request.getSourceDwg()); updated = true; }
        if (request.getFileResource() != null && !Objects.equals(svg.getFileResource(), request.getFileResource())) { svg.setFileResource(request.getFileResource()); updated = true; }
        if (request.getSvgKind() != null && !Objects.equals(svg.getSvgKind(), request.getSvgKind())) { svg.setSvgKind(request.getSvgKind()); updated = true; }
        if (request.getSelectedAreaJson() != null && !Objects.equals(svg.getSelectedAreaJson(), request.getSelectedAreaJson())) { svg.setSelectedAreaJson(request.getSelectedAreaJson()); updated = true; }
        if (request.getWidth() != null && !Objects.equals(svg.getWidth(), request.getWidth())) { svg.setWidth(request.getWidth()); updated = true; }
        if (request.getHeight() != null && !Objects.equals(svg.getHeight(), request.getHeight())) { svg.setHeight(request.getHeight()); updated = true; }
        return updated;
    }

    @Transactional
    public BuildingGlb registerGlb(BuildingGlbCreate request, SecurityContext securityContext) {
        BuildingGlb glb = new BuildingGlb();
        glb.setId(UUID.randomUUID().toString());
        updateGlbNoMerge(glb, request);
        BaseclassService.createSecurityObjectNoMerge(glb, securityContext);
        BuildingBundle bundle = request.getBuildingBundle();
        if (bundle != null) {
            bundle.setActiveGlb(glb).setStatus(STATUS_READY);
        }
        BuildingBundleFile file = createBundleFileNoMerge(bundle, request.getFileResource(), "GLB", BuildingGlb.class.getCanonicalName(), glb.getId(), true, request.getExternalId(), securityContext);
        mergeNonNull(glb, bundle, file);
        return glb;
    }

    public boolean updateGlbNoMerge(BuildingGlb glb, BuildingGlbCreate request) {
        boolean updated = basicService.updateBasicNoMerge(request, glb);
        if (request.getExternalId() != null && !Objects.equals(glb.getExternalId(), request.getExternalId())) { glb.setExternalId(request.getExternalId()); updated = true; }
        if (request.getBuildingBundle() != null && !Objects.equals(glb.getBuildingBundle(), request.getBuildingBundle())) { glb.setBuildingBundle(request.getBuildingBundle()); updated = true; }
        if (request.getSourceDwg() != null && !Objects.equals(glb.getSourceDwg(), request.getSourceDwg())) { glb.setSourceDwg(request.getSourceDwg()); updated = true; }
        if (request.getSourceSvg() != null && !Objects.equals(glb.getSourceSvg(), request.getSourceSvg())) { glb.setSourceSvg(request.getSourceSvg()); updated = true; }
        if (request.getFileResource() != null && !Objects.equals(glb.getFileResource(), request.getFileResource())) { glb.setFileResource(request.getFileResource()); updated = true; }
        if (request.getGlbKind() != null && !Objects.equals(glb.getGlbKind(), request.getGlbKind())) { glb.setGlbKind(request.getGlbKind()); updated = true; }
        if (request.getCameraPointsJson() != null && !Objects.equals(glb.getCameraPointsJson(), request.getCameraPointsJson())) { glb.setCameraPointsJson(request.getCameraPointsJson()); updated = true; }
        return updated;
    }

    @Transactional
    public BuildingFloorResource linkFloorResource(BuildingFloorResourceCreate request, SecurityContext securityContext) {
        BuildingFloorResource link = new BuildingFloorResource();
        link.setId(UUID.randomUUID().toString());
        basicService.updateBasicNoMerge(request, link);
        link.setBuildingFloor(request.getBuildingFloor());
        link.setBundleFile(request.getBundleFile());
        link.setResourcePurpose(request.getResourcePurpose());
        link.setActiveForPurpose(Boolean.TRUE.equals(request.getActiveForPurpose()));
        BaseclassService.createSecurityObjectNoMerge(link, securityContext);
        repository.merge(link);
        return link;
    }

    @Transactional
    public BuildingStudioConversionResponse runConversion(BuildingStudioConversionRequest request, SecurityContext securityContext) {
        BuildingDwg dwg = request.getBuildingDwg();
        if (dwg == null || dwg.getFileResource() == null || dwg.getFileResource().getFullPath() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "buildingDwg must have source FileResource with fullPath");
        }
        BuildingBundle bundle = dwg.getBuildingBundle();
        dwg.setConversionStatus(STATUS_CONVERTING);
        if (bundle != null) {
            bundle.setStatus(STATUS_CONVERTING);
        }
        mergeNonNull(dwg, bundle);

        String jobId = UUID.randomUUID().toString();
        Path jobDir = Path.of(workDir, jobId);
        Path resultJson = jobDir.resolve("result.json");
        try {
            Files.createDirectories(jobDir);
            BuildingStudioProcessResult result = executeRunner(request, jobId, jobDir, resultJson);
            return persistConversionResult(dwg, bundle, result, jobId, securityContext);
        } catch (Exception e) {
            dwg.setConversionStatus(STATUS_FAILED).setConversionLog(e.getMessage()).setConvertedAt(OffsetDateTime.now());
            if (bundle != null) {
                bundle.setStatus(STATUS_FAILED);
            }
            mergeNonNull(dwg, bundle);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Building Studio conversion failed: " + e.getMessage(), e);
        }
    }

    private BuildingStudioProcessResult executeRunner(BuildingStudioConversionRequest request, String jobId, Path jobDir, Path resultJson) throws IOException, InterruptedException {
        File runner = new File(runnerPath);
        if (!runner.exists()) {
            throw new IOException("building.studio.runnerPath does not exist: " + runnerPath);
        }
        List<String> command = new ArrayList<>();
        command.add(pythonExecutable);
        command.add(runnerPath);
        command.add("--input");
        command.add(request.getBuildingDwg().getFileResource().getFullPath());
        command.add("--work-dir");
        command.add(jobDir.toAbsolutePath().toString());
        command.add("--job-id");
        command.add(jobId);
        command.add("--result-json");
        command.add(resultJson.toAbsolutePath().toString());
        command.add("--output-version");
        command.add(Optional.ofNullable(request.getOutputVersion()).orElse("ACAD2018"));
        if (odaExecutable != null && !odaExecutable.isBlank()) {
            command.add("--oda");
            command.add(odaExecutable);
        }
        if (Boolean.TRUE.equals(request.getExportSvg())) command.add("--export-svg");
        if (Boolean.TRUE.equals(request.getExportGlb())) command.add("--export-glb");
        if (Boolean.TRUE.equals(request.getExportPng())) command.add("--export-png");

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        builder.directory(jobDir.toFile());
        Process process = builder.start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        boolean finished = process.waitFor(conversionTimeoutSeconds, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new IOException("conversion timed out after " + conversionTimeoutSeconds + " seconds. Output: " + tail(output));
        }
        if (process.exitValue() != 0) {
            throw new IOException("conversion exited with " + process.exitValue() + ". Output: " + tail(output));
        }
        if (!Files.exists(resultJson)) {
            throw new IOException("conversion did not write result json. Output: " + tail(output));
        }
        BuildingStudioProcessResult result = objectMapper.readValue(resultJson.toFile(), BuildingStudioProcessResult.class);
        if (result.getLog() == null) {
            result.setLog(output);
        }
        return result;
    }

    private BuildingStudioConversionResponse persistConversionResult(BuildingDwg dwg, BuildingBundle bundle, BuildingStudioProcessResult result, String jobId, SecurityContext securityContext) throws Exception {
        List<Object> toMerge = new ArrayList<>();
        List<BuildingBundleFile> files = new ArrayList<>();
        BuildingSvg svg = null;
        BuildingGlb glb = null;

        dwg.setConverterJobId(jobId).setConversionLog(result.getLog()).setConvertedAt(OffsetDateTime.now()).setConversionStatus(STATUS_READY);
        if (bundle != null) bundle.setStatus(STATUS_READY);

        if (result.getDxfPath() != null) {
            FileResource dxf = createFileResourceForPath(Path.of(result.getDxfPath()), "DXF", securityContext);
            dwg.setDxfFileResource(dxf);
            BuildingBundleFile dxfLink = createBundleFileNoMerge(bundle, dxf, "DXF", BuildingDwg.class.getCanonicalName(), dwg.getId(), true, dwg.getExternalId() + "-dxf", securityContext);
            if (dxfLink != null) files.add(dxfLink);
            toMerge.add(dxf);
        }
        if (result.getSvgPath() != null) {
            FileResource svgResource = createFileResourceForPath(Path.of(result.getSvgPath()), "SVG", securityContext);
            svg = new BuildingSvg();
            svg.setId(UUID.randomUUID().toString());
            svg.setBuildingBundle(bundle);
            svg.setSourceDwg(dwg);
            svg.setFileResource(svgResource);
            svg.setExternalId(dwg.getExternalId() + "-svg");
            svg.setSvgKind("MAIN");
            svg.setWidth(result.getSvgWidth());
            svg.setHeight(result.getSvgHeight());
            svg.setSelectedAreaJson(result.getSelectedAreaJson());
            BaseclassService.createSecurityObjectNoMerge(svg, securityContext);
            if (bundle != null) bundle.setActiveSvg(svg);
            BuildingBundleFile svgLink = createBundleFileNoMerge(bundle, svgResource, "SVG", BuildingSvg.class.getCanonicalName(), svg.getId(), true, svg.getExternalId(), securityContext);
            if (svgLink != null) files.add(svgLink);
            toMerge.add(svgResource); toMerge.add(svg);
        }
        if (result.getGlbPath() != null) {
            FileResource glbResource = createFileResourceForPath(Path.of(result.getGlbPath()), "GLB", securityContext);
            glb = new BuildingGlb();
            glb.setId(UUID.randomUUID().toString());
            glb.setBuildingBundle(bundle);
            glb.setSourceDwg(dwg);
            glb.setSourceSvg(svg);
            glb.setFileResource(glbResource);
            glb.setExternalId(dwg.getExternalId() + "-glb");
            glb.setGlbKind("WALKTHROUGH");
            glb.setCameraPointsJson(result.getCameraPointsJson());
            BaseclassService.createSecurityObjectNoMerge(glb, securityContext);
            if (bundle != null) bundle.setActiveGlb(glb);
            BuildingBundleFile glbLink = createBundleFileNoMerge(bundle, glbResource, "GLB", BuildingGlb.class.getCanonicalName(), glb.getId(), true, glb.getExternalId(), securityContext);
            if (glbLink != null) files.add(glbLink);
            toMerge.add(glbResource); toMerge.add(glb);
        }
        if (result.getPngPath() != null) {
            FileResource pngResource = createFileResourceForPath(Path.of(result.getPngPath()), "PNG", securityContext);
            BuildingBundleFile pngLink = createBundleFileNoMerge(bundle, pngResource, "PNG", null, null, false, dwg.getExternalId() + "-png", securityContext);
            if (pngLink != null) files.add(pngLink);
            toMerge.add(pngResource);
        }
        toMerge.add(dwg);
        if (bundle != null) toMerge.add(bundle);
        toMerge.addAll(files);
        repository.massMerge(toMerge);
        return new BuildingStudioConversionResponse().setBuildingBundle(bundle).setBuildingDwg(dwg).setSvg(svg).setGlb(glb).setFiles(files).setStatus(STATUS_READY).setLogTail(tail(result.getLog()));
    }

    private BuildingBundleFile createBundleFileNoMerge(BuildingBundle bundle, FileResource fileResource, String type, String entityType, String entityId, boolean defaultForType, String externalId, SecurityContext securityContext) {
        if (bundle == null || fileResource == null) return null;
        BuildingBundleFile file = new BuildingBundleFile();
        file.setId(UUID.randomUUID().toString());
        file.setBuildingBundle(bundle).setFileResource(fileResource).setArtifactType(type).setArtifactEntityType(entityType).setArtifactEntityId(entityId).setDefaultForType(defaultForType).setExternalId(externalId);
        file.setName(type + " " + Optional.ofNullable(fileResource.getOriginalFilename()).orElse(fileResource.getName()));
        BaseclassService.createSecurityObjectNoMerge(file, securityContext);
        return file;
    }

    private FileResource createFileResourceForPath(Path path, String type, SecurityContext securityContext) throws Exception {
        if (!Files.exists(path)) {
            throw new IOException("expected " + type + " output was not found: " + path);
        }
        String name = path.getFileName().toString();
        FileResourceCreate create = new FileResourceCreate()
                .setFullPath(path.toAbsolutePath().toString())
                .setActualFilename(name)
                .setOriginalFilename(name)
                .setOffset(Files.size(path))
                .setMd5(md5(path));
        create.setName(name);
        return fileResourceService.createFileResourceNoMerge(create, securityContext);
    }

    private String md5(Path path) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] data = Files.readAllBytes(path);
        byte[] hash = digest.digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private String tail(String value) {
        if (value == null) return null;
        return value.length() <= 4000 ? value : value.substring(value.length() - 4000);
    }

    private void mergeNonNull(Object... values) {
        List<Object> nonNull = new ArrayList<>();
        for (Object value : values) {
            if (value != null) {
                nonNull.add(value);
            }
        }
        if (!nonNull.isEmpty()) {
            repository.massMerge(nonNull);
        }
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) { return repository.getByIdOrNull(id, c, securityContext); }
    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) { return repository.listByIds(c, ids, securityContext); }
    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> attr, SecurityContext securityContext) { return repository.getByIdOrNull(id, c, attr, securityContext); }
    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> attr, SecurityContext securityContext) { return repository.listByIds(c, ids, attr, securityContext); }
}
