package com.icome.web.winner.action;

import com.icome.pojo.Winners;
import com.icome.service.IWinnersService;
import com.icome.web.common.Response;
import com.youguu.core.util.json.YouguuJsonHelper;
import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * describe:
 *
 * @date: 2019/01/06 20:30:20:30
 * @version: V1.0
 * @review:
 */

@SwaggerDefinition(
        tags = {
                @Tag(name = "v1.0", description = "获奖者查询")
        }
)
@Api(value = "WinnerAction")
@Path(value = "winner")
@Controller("winnerAction")
public class WinnerAction {

    @Resource
    private IWinnersService winnersService;

    @GET
    @Path(value = "/listByLevel")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "根据奖项等级查询获奖者", notes = "返回某一个奖项下的所有获奖者", author = "更新于 2019-01-06")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "查询成功", response = Response.class)
    })
    public String listByLevel(@ApiParam(value = "奖项等级", required = true) @QueryParam("luckLevel") int luckLevel) {
        try {
            List<Winners> list = winnersService.listByLevel(luckLevel);
            if(null == list){
                return YouguuJsonHelper.returnJSON("0002", "无获奖名单");
            }
            return YouguuJsonHelper.returnJSON("0000", "查询成功", list);
        } catch (Exception e) {
            return YouguuJsonHelper.returnJSON("0001", "查询失败，请重试");
        }
    }

}
