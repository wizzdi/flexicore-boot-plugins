package com.wizzdi.video.conference.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.video.conference.model.Room;
import com.wizzdi.video.conference.model.VideoServer;
import com.wizzdi.video.conference.model.VideoServerUser;

public class JitsiTokenRequest {

    private String roomId;
    @JsonIgnore
    private Room room;
    @JsonIgnore
    private VideoServerUser videoServerUser;

    public String getRoomId() {
        return roomId;
    }

    public <T extends JitsiTokenRequest> T setRoomId(String roomId) {
        this.roomId = roomId;
        return (T) this;
    }

    @JsonIgnore
    public Room getRoom() {
        return room;
    }

    public <T extends JitsiTokenRequest> T setRoom(Room room) {
        this.room = room;
        return (T) this;
    }

    @JsonIgnore
    public VideoServerUser getVideoServerUser() {
        return videoServerUser;
    }

    public <T extends JitsiTokenRequest> T setVideoServerUser(VideoServerUser videoServerUser) {
        this.videoServerUser = videoServerUser;
        return (T) this;
    }
}
