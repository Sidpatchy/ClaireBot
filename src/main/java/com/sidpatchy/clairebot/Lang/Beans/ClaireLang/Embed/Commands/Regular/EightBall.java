package com.sidpatchy.clairebot.Lang.Beans.ClaireLang.Embed.Commands.Regular;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EightBall {
    @JsonProperty("8bResponses")
    public List<String> eightballResponses;
    @JsonProperty("8bRiggedResponses")
    public List<String> eightBallRiggedResponses;
}
