package com.video.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

public class PayLoad {
    @Getter
    @Setter
    @ApiModelProperty(notes = "File Path To pick the Video File", name = "file", required = true, example = "/Users/bhuvanesh/Downloads/sample-wmv.wmv")
    private String file;
}
