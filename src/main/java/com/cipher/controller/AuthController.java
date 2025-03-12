package com.cipher.controller;

import com.cipher.auth.JWTProvider;
import com.cipher.dto.AuthDto;
import com.cipher.dto.BasicDto;
import com.cipher.dto.FaceIdDto;
import com.cipher.dto.LoginDto;
import com.cipher.exception.ForbiddenException;
import com.cipher.service.FaceIdService;
import com.cipher.service.LogInOutService;
import com.cipher.service.UserService;
import com.cipher.util.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JWTProvider jwtProvider;
    @Autowired
    protected AuthenticationManager authenticationManager;
    @Autowired
    private LogInOutService logInOutService;
    @Autowired
    private FaceIdService faceIdService;
    Logger logger = LoggerFactory.getLogger(AuthController.class);


    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity login(@RequestBody LoginDto user) throws Exception {
        BasicDto dto = new BasicDto();
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        LocalDateTime nowTime = LocalDateTime.now();

        authenticate(user, token, nowTime);

        List<String> programCodeList = new ArrayList<>();
        List<GrantedAuthority> authList = new ArrayList<>();

        authList = AuthorityUtils.createAuthorityList(programCodeList.toArray(new String[0]));
        logInOutService.createLogin(user.getUserName(), nowTime);

        AuthDto result = new AuthDto(jwtProvider.generateToken(new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authList)), true);

        return ResponseEntity.ok().body(result);
    }


    private void authenticate(LoginDto request, UsernamePasswordAuthenticationToken token, LocalDateTime nowTime){
        try {
            authenticationManager.authenticate(token);
        } catch (Exception e) {
            e.printStackTrace();
            logInOutService.createLoginFail(token.getName(),nowTime);
            throw new ForbiddenException(Message.loginFail);
        }
    }

    @RequestMapping(value = "/faceid", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getObjId(@RequestBody() FaceIdDto faceIdDto) {
        try {
            String result = faceIdService.identifyFaceId(faceIdDto.getUsername(), faceIdDto.getFacePicStr());
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = "/faceid2", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getObjId2(@RequestHeader("X-username") String username, @RequestParam("files") MultipartFile[] uploadFiles) {
        try {
            byte[] fileContent = uploadFiles[0].getBytes();
            String result = faceIdService.identifyFaceId(username, Base64.getEncoder().encodeToString(fileContent));
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
