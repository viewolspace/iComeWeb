package com.icome.web.luck.action;

import com.icome.web.common.Response;
import com.youguu.core.util.json.YouguuJsonHelper;
import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@SwaggerDefinition(
        tags = {
                @Tag(name = "v1.0", description = "业务员信息维护")
        }
)
@Api(value = "LuckDrawAction")
@Path(value = "luck")
@Controller("luckDrawAction")
public class LuckDrawAction {

    @POST
    @Path(value = "/luckDraw")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "抽奖接口", notes = "抽奖接口", author = "更新于 2019-01-03")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "抽奖成功", response = Response.class)
    })
    public String luckDraw(@ApiParam(value = "用户ID", required = true) @FormParam("userId") int userId) {
        try {

            return YouguuJsonHelper.returnJSON("0000", "抽奖成功");
        } catch (Exception e) {
            return YouguuJsonHelper.returnJSON("0001", "抽奖成功");
        }
    }

}
