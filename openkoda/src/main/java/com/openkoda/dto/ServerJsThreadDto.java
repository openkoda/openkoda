package com.openkoda.dto;

public class ServerJsThreadDto {

    public Thread.State state;
    public long id;

    public ServerJsThreadDto(Thread thread) {
       this.state=thread.getState();
       this.id=thread.getId();
    }
}