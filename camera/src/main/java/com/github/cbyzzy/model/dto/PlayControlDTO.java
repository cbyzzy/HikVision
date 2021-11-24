package com.github.cbyzzy.model.dto;

public class PlayControlDTO {

    //云台控制动作时间
    private Integer mill;

    //控制指令
    private String command;

    public PlayControlDTO() {}

    public PlayControlDTO(Integer mill,String command) {
        this.mill = mill;
        this.command = command;
    }

    public Integer getMill() { return mill; }
    public void setMill(Integer mill) { this.mill = mill; }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
}
