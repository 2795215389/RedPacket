package com.js.main.consumer.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.js.api.dto.RedPacketDto;
import com.js.api.response.BaseResponse;
import com.js.api.response.StatusCode;
import com.js.api.service.IRedPacketService;
import com.js.main.provider.service.RedPacketServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class RedPacketController {
    private static final Logger log= LoggerFactory.getLogger(RedPacketController.class);
    private static final String urlPrefix="/red/packet";
    @Value("${server.port}")
    private String getPort;


    @Reference(
            version = "1.0.0",interfaceName = "com.js.api.service.IRedPacketService",
            interfaceClass = com.js.api.service.IRedPacketService.class,
            timeout =120000
    )
    IRedPacketService redPacketService;


    @PostMapping(value = urlPrefix+"/hand/out",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse handOut(@Validated @RequestBody RedPacketDto dto, BindingResult result){
        if(result.hasErrors()){
            return new BaseResponse(StatusCode.InvalidParm);
        }
        BaseResponse response=new BaseResponse(StatusCode.Failed);
        String redId=redPacketService.handout(dto);
        if(redId!=null){
            response=new BaseResponse(StatusCode.Success);
            response.setData(redId);
        }else{
            log.info("发红包失败！");
        }
        return response;
    }




    @GetMapping(value = urlPrefix+"/rob")
    public BaseResponse rob(Integer userId,String redId){
        BaseResponse response=new BaseResponse(StatusCode.Failed);
        try{
            BigDecimal res=redPacketService.rob(userId, redId);
            if(res!=null){
                response=new BaseResponse(StatusCode.Success);
                response.setData(res);
                return response;
            }else{
                log.info("抢红包失败，可能是红包已被抢光！");
                return response;
            }
        }catch (Exception e){
            log.info("抢红包失败，可能是服务中的方法执行错误！");
            return response;
        }
    }


    @GetMapping(value = urlPrefix+"/robLock")
    public BaseResponse robLock(Integer userId,String redId){
        BaseResponse response=new BaseResponse(StatusCode.Failed);
        try{
            String msg="服务器端口号为:"+getPort;
            BigDecimal res=redPacketService.robLock(userId, redId);
            if(res!=null){
                response=new BaseResponse(StatusCode.Success);
                response.setData(res);
                System.out.println(msg);
                return response;
            }else{
                log.info("抢红包失败，可能是红包已被抢光！");
                return response;
            }
        }catch (Exception e){
            log.info("抢红包失败，可能是服务中的方法执行错误！");
            return response;
        }
    }
}
