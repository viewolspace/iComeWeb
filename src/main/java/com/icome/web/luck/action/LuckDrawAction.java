package com.icome.web.luck.action;

import com.icome.exception.ServiceException;
import com.icome.pojo.Luck;
import com.icome.pojo.Winners;
import com.icome.service.ILuckService;
import com.icome.service.IUserService;
import com.icome.web.common.Response;
import com.youguu.core.util.json.YouguuJsonHelper;
import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.ws.rs.*;
import java.util.List;

@SwaggerDefinition(
        tags = {
                @Tag(name = "v1.0", description = "业务员信息维护")
        }
)
@Api(value = "LuckDrawAction")
@Path(value = "luck")
@Controller("luckDrawAction")
public class LuckDrawAction {

    @Resource
    private ILuckService luckService;
    @Resource
    private IUserService userService;

    @GET
    @Path(value = "/queryLuckInfo")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "查询当前要抽的奖品信息", notes = "只返回一条奖品信息", author = "更新于 2019-01-05")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "查询成功", response = Response.class)
    })
    public String queryLuckInfo(@ApiParam(value = "奖项等级", required = true) @QueryParam("luckLevel") int luckLevel) {
        try {
            Luck luck = luckService.getLuck(luckLevel);
            if(null == luck){
                return YouguuJsonHelper.returnJSON("0002", "奖项不存在");
            }
            int total = userService.queryTotalNum();
            luck.setJoinNum(total);
            return YouguuJsonHelper.returnJSON("0000", "查询成功", luck);
        } catch (Exception e) {
            return YouguuJsonHelper.returnJSON("0001", "查询失败，请重试");
        }
    }

    @POST
    @Path(value = "/luckDraw")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "抽奖接口", notes = "抽奖接口", author = "更新于 2019-01-03")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "抽奖成功", response = Response.class)
    })
    public String luckDraw(@ApiParam(value = "奖品ID", required = true) @FormParam("luckId") int luckId,
                           @ApiParam(value = "参与人数", required = true) @FormParam("joinNum") int joinNum,
                           @ApiParam(value = "抽取人数", required = true) @FormParam("rewardNum") int rewardNum) {
        try {
            List<Winners> list = luckService.luckDraw(luckId, joinNum, rewardNum);

            if(null == list){
                return YouguuJsonHelper.returnJSON("0002", "抽奖失败，刷新页面重试");
            }

            return YouguuJsonHelper.returnJSON("0000", "抽奖成功", list);
        } catch (ServiceException e) {
            e.printStackTrace();
            return YouguuJsonHelper.returnJSON("0001", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return YouguuJsonHelper.returnJSON("0001", "抽奖失败");
        }
    }

}
