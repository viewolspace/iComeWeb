package com.icome.web.user.action;

import com.alibaba.fastjson.JSONObject;
import com.icome.api.IComeApi;
import com.icome.pojo.User;
import com.icome.pojo.query.UserQuery;
import com.icome.service.IUserService;
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
 * 用户相关
 */

@SwaggerDefinition(
        tags = {
                @Tag(name = "v1.0", description = "用户相关接口")
        }
)
@Api(value = "UserAction")
@Path(value = "user")
@Controller("UserAction")
public class UserAction {

    @Resource
    private IUserService userService;

    @Resource
    private IComeApi comeApi;

    /**
     * 根据ticket查询用户信息
     *
     * @return
     */
    @GET
    @Path(value = "/userDetail")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "查询用户基本数据", notes = "查询用户基本数据", author = "更新于 2019-01-03")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = Response.class)
    })
    public String userDetail(@ApiParam(value = "用户令牌", required = true) @QueryParam("ticket") String ticket) {

        JSONObject data = comeApi.getUserDetail(ticket);

        if(data!=null){
            String eId = data.getString("eId");
            User user = userService.getUser(eId);
            if(user!=null){
                return YouguuJsonHelper.returnJSON("0000","ok",user);
            }

        }

        return YouguuJsonHelper.returnJSON("0001","获取用户数据出错");


    }

    /**
     * 用户签到
     *
     * @return
     */
    @GET
    @Path(value = "/signed")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "查询用户基本数据", notes = "查询用户基本数据", author = "更新于 2019-01-03")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = Response.class)
    })
    public String userSigned(@ApiParam(value = "用户令牌", required = true) @QueryParam("ticket") String ticket) {

        JSONObject data = comeApi.getUserDetail(ticket);

        if(data!=null){
            String eId = data.getString("eId");
            User user = userService.getUser(eId);
            if(user!=null){
                return YouguuJsonHelper.returnJSON("0000","已经签到",user);
            }else {
                user = new User();
                user.setUrl("http://im2.enn.cn/fileserver/photo/getEmpPhoto?eId=" + eId);
                user.setUserName(data.getString("eName"));
                user.setStatus(1);
                user.setThirdId(eId);
                userService.saveUser(user);
                return YouguuJsonHelper.returnJSON("0000","ok",user);
            }

        }

        return YouguuJsonHelper.returnJSON("0000","ok");


    }

    /**
     * 查询新增的签到用户
     *
     * @return
     */
    @GET
    @Path(value = "/userList")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "查询新增的签到用户", notes = "查询新增的签到用户", author = "更新于 2019-01-03")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = Response.class)
    })
    public String userList(@ApiParam(value = "用户id", required = true) @QueryParam("userId") int userId) {

        UserQuery userQuery = new UserQuery();

        userQuery.setLastMaxUid(userId);

        List<User> list = userService.queryUser(userQuery);

        return YouguuJsonHelper.returnJSON("0000","ok","result",list);
    }



    /**
     * 查询全部签到用户
     *
     * @return
     */
    @GET
    @Path(value = "/allUser")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "查询全部签到用户", notes = "查询全部签到用户", author = "更新于 2019-01-03")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = Response.class)
    })
    public String allUser() {
        UserQuery userQuery = new UserQuery();
        userQuery.setStatus(1);
        List<User> list = userService.queryUser(userQuery);
        return YouguuJsonHelper.returnJSON("0000","ok","result",list);
    }






}
