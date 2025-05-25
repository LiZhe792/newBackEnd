package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.User;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.UserRepository;
import cn.edu.sdu.java.server.repositorys.UserTypeRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/secure/user")
public class UserController {
    @Autowired
    private UserTypeRepository userTypeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @PostMapping("/getUser")
    public DataResponse getUserType(@Valid @RequestBody DataRequest dataRequest){
        Integer userId = CommonMethod.getPersonId();
        User u;

        Optional<User> op = userRepository.findByPersonId(userId);
        Integer typeId;
        if (op.isPresent()) {
            u = op.get();
            typeId = u.getUserType().getId();
        } else {
            return CommonMethod.getReturnMessageError("该用户不存在！");
        }
        String personNum =u.getPerson().getNum();
        Map m=new HashMap<>();
        m.put("typeId",typeId.equals(2)?"学生":"管理员");
        m.put("personNum",personNum);
        m.put("userName",u.getPerson().getName());
        Integer personId=u.getPerson().getPersonId();
        if(personId==null){
            personId=0;
        }
        m.put("personId",personId);
        if(typeId.equals(2)){
            Optional<Student> op1=studentRepository.findByPersonId(personId);
            Student student=op1.isPresent()?op1.get():new Student();
            m.put("student",student);
        }
        return CommonMethod.getReturnData(m);
    }
}
