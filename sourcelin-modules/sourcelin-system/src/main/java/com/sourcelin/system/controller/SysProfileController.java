package com.sourcelin.system.controller;

import java.util.Arrays;

import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.file.FileTypeUtils;
import com.sourcelin.common.core.utils.file.MimeTypeUtils;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckLogin;
import com.sourcelin.common.security.stp.StpAdminUtil;
import com.sourcelin.file.api.service.RemoteFileService;
import com.sourcelin.file.api.domain.SysFile;
import com.sourcelin.file.api.domain.FileConfirmRequest;
import com.sourcelin.system.api.domain.SysUser;
import com.sourcelin.system.api.model.SysLoginUser;
import com.sourcelin.system.domain.vo.UserAvatarUploadVo;
import com.sourcelin.system.domain.vo.UserProfileVo;

/**
 * 个人信息 业务处理
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/user/profile")
public class SysProfileController extends BaseController
{
    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private RemoteFileService remoteFileService;

    /**
     * 个人信息
     */
    @SaCheckLogin(type = "admin")
    @GetMapping
    public UserProfileVo profile()
    {
        SysLoginUser loginUser = currentLoginUser();
        if (loginUser == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未获取到登录用户");
        }
        SysUser user = resolveCurrentUser(loginUser);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "当前登录用户不存在或已失效");
        }
        String username = user.getUserName();
        return UserProfileVo.builder()
            .user(user)
            .roleGroup(userService.selectUserRoleGroup(username))
            .postGroup(userService.selectUserPostGroup(username))
            .build();
    }

    /**
     * 修改用户
     */
    @SaCheckLogin(type = "admin")
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public Boolean updateProfile(@RequestBody SysUser user)
    {
        SysLoginUser loginUser = currentLoginUser();
        if (loginUser == null)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未获取到登录用户");
        }
        SysUser currentUser = resolveCurrentUser(loginUser);
        if (currentUser == null)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "当前登录用户不存在或已失效");
        }
currentUser.setNickName(user.getNickName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        currentUser.setRemark(user.getRemark());
        if (StringUtils.isNotEmpty(user.getAvatar()))
        {
            currentUser.setAvatar(user.getAvatar());
        }
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(currentUser))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(currentUser))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        if (userService.updateUserProfile(currentUser) > 0)
        {
            syncSessionLoginUser(loginUser, currentUser);
            return true;
        }
        throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     */
    @SaCheckLogin(type = "admin")
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public Boolean updatePwd(String oldPassword, String newPassword)
    {
        SysLoginUser loginUser = currentLoginUser();
        if (loginUser == null)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未获取到登录用户");
        }
        SysUser user = resolveCurrentUser(loginUser);
        if (user == null)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "当前登录用户不存在或已失效");
        }
        String password = user.getPassword();
        if (!com.sourcelin.common.security.utils.SecurityUtils.matchesPassword(oldPassword, password))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改密码失败，旧密码错误");
        }
        if (com.sourcelin.common.security.utils.SecurityUtils.matchesPassword(newPassword, password))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新密码不能与旧密码相同");
        }
        if (userService.resetUserPwd(user.getUserId(), com.sourcelin.common.security.utils.SecurityUtils.encryptPassword(newPassword)) > 0)
        {
            user.setPassword(com.sourcelin.common.security.utils.SecurityUtils.encryptPassword(newPassword));
            syncSessionLoginUser(loginUser, user);
            return true;
        }
        throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改密码异常，请联系管理员");
    }
    
    /**
     * 头像上传
     */
    @SaCheckLogin(type = "admin")
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public UserAvatarUploadVo avatar(@RequestParam("avatarfile") MultipartFile file)
    {
        if (file.isEmpty())
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "上传图片异常，请选择图片文件");
        }
        SysLoginUser loginUser = currentLoginUser();
        if (loginUser == null)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未获取到登录用户");
        }
        SysUser currentUser = resolveCurrentUser(loginUser);
        if (currentUser == null)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "当前登录用户不存在或已失效");
        }
        String extension = FileTypeUtils.getExtension(file);
        if (!StringUtils.equalsAnyIgnoreCase(extension, MimeTypeUtils.IMAGE_EXTENSION))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "文件格式不正确，请上传" + Arrays.toString(MimeTypeUtils.IMAGE_EXTENSION) + "格式");
        }
        ApiResponse<SysFile> fileResult = remoteFileService.upload(file);
        if (StringUtils.isNull(fileResult)
            || StringUtils.isNull(fileResult.getCode())
            || !ResultCode.SUCCESS.getCode().equals(fileResult.getCode())
            || StringUtils.isNull(fileResult.getData()))
        {
            throw new BusinessException(ResultCode.REMOTE_SERVICE_ERROR, "文件服务异常，请联系管理员");
        }
        String url = fileResult.getData().getUrl();
        Long fileId = fileResult.getData().getFileId();

        FileConfirmRequest confirmRequest = new FileConfirmRequest();
        confirmRequest.setFileId(fileId);
        confirmRequest.setBizType("avatar");
        confirmRequest.setBizId(String.valueOf(currentUser.getUserId()));
        ApiResponse<Void> confirmResult = remoteFileService.confirmFile(fileId, confirmRequest);
        if (StringUtils.isNull(confirmResult)
            || StringUtils.isNull(confirmResult.getCode())
            || !ResultCode.SUCCESS.getCode().equals(confirmResult.getCode()))
        {
            throw new BusinessException(ResultCode.REMOTE_SERVICE_ERROR, "确认头像文件失败");
        }

        if (!userService.updateUserAvatar(currentUser.getUserName(), url, fileId))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "上传图片异常，请联系管理员");
        }
        currentUser.setAvatar(url);
        currentUser.setAvatarFileId(fileId);
        syncSessionLoginUser(loginUser, currentUser);
        return UserAvatarUploadVo.builder()
            .imgUrl(url)
            .avatarFileId(fileId)
            .build();
    }

    private SysLoginUser currentLoginUser() {
        if (!StpAdminUtil.stpLogic.isLogin()) {
            return null;
        }
        Object loginUser = StpAdminUtil.stpLogic.getSession().get(SecurityConstants.LOGIN_USER);
        if (loginUser instanceof SysLoginUser) {
            return (SysLoginUser) loginUser;
        }
        loginUser = StpAdminUtil.stpLogic.getSession().get("loginUser");
        if (loginUser instanceof SysLoginUser) {
            return (SysLoginUser) loginUser;
        }
        return null;
    }

    private SysUser resolveCurrentUser(SysLoginUser loginUser)
    {
        Long userId = loginUser.getUserid();
        if (userId == null && loginUser.getSysUser() != null)
        {
            userId = loginUser.getSysUser().getUserId();
        }
        if (userId != null)
        {
            SysUser user = userService.selectUserById(userId);
            if (user != null)
            {
                return user;
            }
        }

        String username = loginUser.getUsername();
        if (StringUtils.isEmpty(username) && loginUser.getSysUser() != null)
        {
            username = loginUser.getSysUser().getUserName();
        }
        if (StringUtils.isNotEmpty(username))
        {
            return userService.selectUserByUserName(username);
        }
        return null;
    }

    private void syncSessionLoginUser(SysLoginUser loginUser, SysUser currentUser)
    {
        loginUser.setUserid(currentUser.getUserId());
        loginUser.setUsername(currentUser.getUserName());
        loginUser.setSysUser(currentUser);
        StpAdminUtil.stpLogic.getSession().set(SecurityConstants.LOGIN_USER, loginUser);
        StpAdminUtil.stpLogic.getSession().set("loginUser", loginUser);
    }
}
