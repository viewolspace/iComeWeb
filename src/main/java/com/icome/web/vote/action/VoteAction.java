package com.icome.web.vote.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icome.api.IComeApi;
import com.icome.pojo.Program;
import com.icome.pojo.ProgramDetail;
import com.icome.service.IProgramService;
import com.icome.web.common.Response;
import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * 用户相关
 */

@SwaggerDefinition(
        tags = {
                @Tag(name = "v1.0", description = "投票")
        }
)
@Api(value = "voteAction")
@Path(value = "vote")
@Controller("voteAction")
public class VoteAction {

    @Resource
    private IProgramService programService;

    @Resource
    private IComeApi comeApi;

    /**
     * 根据ticket查询用户信息
     *
     * @return
     */
    @GET
    @Path(value = "/list")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "查询投票节目", notes = "查询投票节目", author = "更新于 2019-01-18")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = Response.class)
    })
    public String list(@ApiParam(value = "用户令牌", required = true) @QueryParam("ticket") String ticket) {

        JSONObject result  = new JSONObject();

        result.put("status","0000");

        result.put("message","ok");

        JSONObject data = comeApi.getUserDetail(ticket);

        String eId = null;
        if(data!=null){
            eId = data.getString("eId");
        }

        int vote = 0 ; //0 已经投票   1 未投票

        List<Program> list = programService.list();

        result.put("list",list);

        if(eId!=null){
            ProgramDetail pd =  programService.getProgramDetail(eId);
            if(pd==null){
                vote = 1;
            }else{
                result.put("detail",pd);
            }

        }
        result.put("vote",vote);
        return JSON.toJSONString(result);

    }


    /**
     * 根据ticket查询用户信息
     *
     * @return
     */
    @GET
    @Path(value = "/vote")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "投票", notes = "投票", author = "更新于 2019-01-18")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = Response.class)
    })
    public String vote(@ApiParam(value = "用户令牌", required = true) @QueryParam("ticket") String ticket,
                       @ApiParam(value = "节目id", required = true) @QueryParam("id") int id) {

        JSONObject result  = new JSONObject();

        JSONObject data = comeApi.getUserDetail(ticket);

        if(data==null || "".equals(data)){
            result.put("status","0001");

            result.put("message","无效投票");

            return result.toJSONString();
        }

        String eId = data.getString("eId");


        result.put("status","0000");

        result.put("message","投票成功");
        int voteRes = programService.vote(eId,id);

        if(voteRes > 0 ){
            result.put("status","0000");

            result.put("message","投票成功");
        }else if(voteRes == 0 ){
            result.put("status","0000");

            result.put("message","您已经投票过");
        }
        return JSON.toJSONString(result);

    }

}
