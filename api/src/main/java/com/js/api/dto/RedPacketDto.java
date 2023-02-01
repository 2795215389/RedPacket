package com.js.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RedPacketDto implements Serializable {
    @NotNull
    private String uid;
    @NotNull
    private Integer people;
    @NotNull
    private Integer money;
}
