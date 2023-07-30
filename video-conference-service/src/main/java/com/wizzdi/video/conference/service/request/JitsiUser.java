package com.wizzdi.video.conference.service.request;

public class JitsiUser {
    private String email;
    private String name;
    private String avatar;

    public String getEmail() {
        return email;
    }

    public <T extends JitsiUser> T setEmail(String email) {
        this.email = email;
        return (T) this;
    }

    public String getName() {
        return name;
    }

    public <T extends JitsiUser> T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public String getAvatar() {
        return avatar;
    }

    public <T extends JitsiUser> T setAvatar(String avatar) {
        this.avatar = avatar;
        return (T) this;
    }
}
