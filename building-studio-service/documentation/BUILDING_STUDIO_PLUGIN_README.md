# Building Studio FlexiCore plugin draft

This package adds two server modules:

- `building-studio-model`: JPA entities for DWG/SVG/GLB resources.
- `building-studio-service`: REST controller, persistence service, and conversion runner integration.

## Data model

- `BuildingBundle`: top-level container for all files exported by Building Studio.
- `BuildingDwg`: uploaded source DWG/DXF plus generated DXF FileResource.
- `BuildingSvg`: SVG outputs linked to the bundle and optional source DWG.
- `BuildingGlb`: GLB walkthrough outputs linked to the bundle and optional source SVG/DWG.
- `BuildingBundleFile`: generic file link under the bundle for DWG, DXF, SVG, GLB, PNG, JSON, etc.
- `BuildingFloorResource`: future link from `BuildingFloor` to one or more bundle files, allowing a floor to have SVG, GLB, walkthrough and metadata resources.

## Main endpoints

All endpoints are under `/plugins/BuildingStudio`:

- `POST getAllBuildingBundles`
- `POST createBuildingBundle`
- `PUT updateBuildingBundle`
- `POST registerDwg`
- `POST registerSvg`
- `POST registerGlb`
- `POST runConversion`
- `POST linkFloorResource`

## Conversion runner

The Java service runs an external Python command configured by properties:

```properties
building.studio.workDir=/home/flexicore/building-studio
building.studio.pythonExecutable=python3
building.studio.runnerPath=/home/flexicore/building-studio/building_studio_cli.py
building.studio.odaExecutable=/usr/local/bin/ODAFileConverter-headless
building.studio.conversionTimeoutSeconds=900
```

The included `building_studio_cli.py` is a minimal runner contract. Replace it with the full conversion logic from the existing Building Studio Python application when deploying.

The runner must write JSON containing paths for generated files:

```json
{
  "status": "READY",
  "dxfPath": "/path/out.dxf",
  "svgPath": "/path/building.svg",
  "glbPath": "/path/building.glb",
  "pngPath": "/path/building.png",
  "svgWidth": 1200,
  "svgHeight": 800,
  "log": "conversion log"
}
```
