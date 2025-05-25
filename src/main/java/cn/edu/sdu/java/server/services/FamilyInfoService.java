package cn.edu.sdu.java.server.services;
import cn.edu.sdu.java.server.models.FamilyMember;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.FamilyMemberRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import cn.edu.sdu.java.server.models.*;

import java.util.List;

@Service
public class FamilyInfoService {
    private static final Logger log = LoggerFactory.getLogger(FamilyInfoService.class);
    private final FamilyMemberRepository familyMemberRepository;
    private final StudentRepository studentRepository;

    public FamilyInfoService(FamilyMemberRepository familyMemberRepository,
                             StudentRepository studentRepository) {
        this.familyMemberRepository = familyMemberRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * 将FamilyMember对象转换为Map
     */
    private Map<String, Object> convertToMap(FamilyMember member) {
        Map<String, Object> map = new HashMap<>();
        if (member == null) return map;

        map.put("memberId", member.getMemberId());
        map.put("relation", member.getRelation());
        map.put("name", member.getName()); // 家庭成员姓名
        map.put("studentName", member.getStudent().getPerson().getName()); // 学生姓名
        map.put("age", member.getAge());
        map.put("gender", member.getGender());
        map.put("unit", member.getUnit());
        map.put("personId", member.getStudent().getPersonId());
        map.put("studentNum", member.getStudent().getPerson().getNum());
        return map;
    }

    /**
     * 获取学生的家庭信息列表
     */
    public DataResponse getFamilyMemberList(DataRequest dataRequest) {
        String numName = CommonMethod.getString(dataRequest.getData(), "studentNum");
        List<FamilyMember> familyMembers = familyMemberRepository.findFamilyMemberListByNumName(numName);
        List<Map<String, Object>> dataList = new ArrayList<>();

        for (FamilyMember member : familyMembers) {
            dataList.add(convertToMap(member));
        }

        return CommonMethod.getReturnData(dataList);
    }

    /**
     * 获取单个家庭信息
     */
    public DataResponse getFamilyInfo(DataRequest dataRequest) {
        Map<String, Object> data = dataRequest.getData();
        Integer familyMemberId = CommonMethod.getInteger(data, "familyMemberId");
        Optional<FamilyMember> optionalMember = familyMemberRepository.findById(familyMemberId);
        if (optionalMember.isEmpty()) {
            return CommonMethod.getReturnMessageError("家庭信息不存在");
        }

        return CommonMethod.getReturnData(convertToMap(optionalMember.get()));
    }

    /**
     * 保存或更新家庭信息
     */
    public DataResponse familyMemberSave(DataRequest dataRequest) {
        Map<String, Object> form = dataRequest.getMap("form");
        if (form == null) {
            return CommonMethod.getReturnMessageError("缺少表单数据");
        }
        // 1. 获取学号
        // 2. 根据学号查ID （studentID）
        // 3. 新增、更新
        // 4.1 检查有没有传入ID（当前要处理的这个数据表的ID）

        // 4.1.1 新增：没有传入
        // 4.1.2 数据全都处理到适合插入数据库的对象- new一个FamilyMember对象，把数据全塞进去。
        // 4.1.3 插入数据库 - 数据库会生成一个ID放入之前的familyMember对象中。
        // 4.1.4 返回插入成功 - 让前端刷新数据

        // 4.2.1 更新：有传入ID
        // 4.2.2 传入ID - 检查传入的ID-返回一个数据库的实体对象 在数据库中是不是真正的存在
        // 4.2.3 更新对象的信息- 把处理到这个对象里
        // 4.2.4 更新数据库 - updateByID 数据库会生成一个ID放入之前的familyMember对象中。
        // 4.2.5 返回更新成功 - 让前端刷新数据
        // 学号
        String studentPersonId = CommonMethod.getString(form, "studentNum");

        Optional<Student> studentOptional = studentRepository.findByPersonNum(studentPersonId);
        if (studentOptional.isEmpty()) {
            return CommonMethod.getReturnMessageError("学生不存在");
        }
        // 学生实体
        Student student = studentOptional.get();

        Integer memberId = CommonMethod.getInteger(dataRequest.getData(), "memberId");
        FamilyMember familyMember;

        if (memberId != null) { // 更新
            Optional<FamilyMember> optionalFamilyMember = familyMemberRepository.findById(memberId);
            if (optionalFamilyMember.isEmpty()) {
                return CommonMethod.getReturnMessageError("家庭信息不存在");
            }
            familyMember = optionalFamilyMember.get(); // 正确获取待更新实体

            // 更新属性（保留原有关联关系）
            familyMember.setAge(CommonMethod.getInteger(form, "age"));
            familyMember.setGender(CommonMethod.getString(form, "gender"));
            familyMember.setName(CommonMethod.getString(form, "name"));
            familyMember.setRelation(CommonMethod.getString(form, "relation"));
            familyMember.setUnit(CommonMethod.getString(form, "unit"));

        } else {
            // 4.1.2 数据全都处理到适合插入数据库的对象- new一个FamilyMember对象，把数据全塞进去。

            familyMember = new FamilyMember();
            familyMember.setAge(CommonMethod.getInteger(form, "age"));
            familyMember.setGender(CommonMethod.getString(form, "gender"));
            familyMember.setName(CommonMethod.getString(form, "name"));
            familyMember.setRelation(CommonMethod.getString(form, "relation"));
            // 工作单位
            familyMember.setUnit(CommonMethod.getString(form, "unit"));

            familyMember.setStudent(student);

        }

        FamilyMember save = familyMemberRepository.save(familyMember);
        return CommonMethod.getReturnData(convertToMap(save));
    }

    /**
     * 删除家庭信息
     */
    public DataResponse familyMemberDelete(DataRequest dataRequest) {
        Integer memberId = dataRequest.getInteger("memberId");
        if (memberId == null) {
            return CommonMethod.getReturnMessageError("缺少家庭信息ID");
        }

        Optional<FamilyMember> optionalMember = familyMemberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            return CommonMethod.getReturnMessageError("家庭信息不存在");
        }

        familyMemberRepository.delete(optionalMember.get());
        return CommonMethod.getReturnMessageOK();
    }

    /**
     * 导出学生家庭信息Excel
     */
    public ResponseEntity<StreamingResponseBody> exportFamilyInfoExcel(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");


        List<FamilyMember> familyMembers = familyMemberRepository.findByStudentPersonId(personId);

        // 创建Excel工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("家庭信息");

        // 设置列宽
        int[] columnWidths = {10, 15, 20, 10, 8, 15, 20};
        for (int i = 0; i < columnWidths.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i] * 256);
        }

        // 创建标题行
        String[] headers = {"序号", "学生学号", "学生姓名", "关系", "姓名", "性别", "单位"};
        XSSFRow headerRow = sheet.createRow(0);
        XSSFCellStyle headerStyle = CommonMethod.createCellStyle(workbook, 12);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 填充数据
        XSSFCellStyle dataStyle = CommonMethod.createCellStyle(workbook, 10);
        for (int i = 0; i < familyMembers.size(); i++) {
            FamilyMember member = familyMembers.get(i);
            XSSFRow dataRow = sheet.createRow(i + 1);

            dataRow.createCell(0).setCellValue(i + 1);
            dataRow.createCell(1).setCellValue(member.getStudent().getPerson().getNum());
            dataRow.createCell(2).setCellValue(member.getStudent().getPerson().getName());
            dataRow.createCell(3).setCellValue(member.getRelation());
            dataRow.createCell(4).setCellValue(member.getName());
            dataRow.createCell(5).setCellValue(member.getGender());
            dataRow.createCell(6).setCellValue(member.getUnit());

            // 设置数据行样式
            for (int j = 0; j < headers.length; j++) {
                dataRow.getCell(j).setCellStyle(dataStyle);
            }
        }

        // 返回响应流
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            byte[] bytes = out.toByteArray();

            return ResponseEntity.ok()
                    .contentType(CommonMethod.exelType)
                    .header("Content-Disposition", "attachment; filename=family_info.xlsx")
                    .body(outputStream -> outputStream.write(bytes));
        } catch (IOException e) {
            log.error("导出家庭信息Excel失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}