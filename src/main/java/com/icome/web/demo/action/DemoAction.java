package com.icome.web.demo.action;

import com.icome.web.common.Response;
import com.youguu.core.util.json.YouguuJsonHelper;
import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * describe:
 *
 * @date: 2019/01/03 23:17:23:17
 * @version: V1.0
 * @review:
 */

@SwaggerDefinition(
        tags = {
                @Tag(name = "v1.0", description = "Demo测试接口")
        }
)
@Api(value = "DemoAction")
@Path(value = "demo")
@Controller("demoAction")
public class DemoAction {

    /**
     * 测试接口
     *
     * @return
     */
    @GET
    @Path(value = "/welcome")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "测试接口", notes = "测试接口，空方法。", author = "更新于 2019-01-03")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = Response.class)
    })
    public String welcome() {

        return YouguuJsonHelper.returnJSON("0000", "hello iCome!");
    }

}
