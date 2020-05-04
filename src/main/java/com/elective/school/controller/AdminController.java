package com.elective.school.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.elective.school.entity.Academy;
import com.elective.school.entity.Course;
import com.elective.school.entity.Student;
import com.elective.school.entity.Teacher;
import com.elective.school.entity.Term;
import com.elective.school.service.Impl.AdminServiceImpl;
import com.elective.school.util.Constant;
import com.elective.school.util.Method;

@Validated
@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	AdminServiceImpl adminService;

	@PostMapping("")
	public String login(@RequestParam(name = "username", required = false) String username,
			@RequestParam(value = "password", required = false) String password, Map<String, Object> map) {
		if (adminService.login(username, password))
			return "admin";
		map.put("msg", "账号或密码错误！！！");
		return "forward:";
	}

	@GetMapping("/term")
	public String term(Map<String, Object> map) {
		map.putAll(adminService.getTerms());
		return "admin";
	}

	@PostMapping("/term")
	public String termAdd(Map<String, Object> map, @RequestParam(name = "termName") String termName) {
		if (adminService.exist(termName)) {
			map.put("error", "该学期已存在，请重新添加");
			map.putAll(adminService.getTerms());
			return "admin";
		}
		if (termName.matches(Constant.reg_term_status)) {
			Term t = new Term(null, termName, 0);
			adminService.save(t);
			map.put("success", "添加成功");
			map.putAll(adminService.getTerms());
			return "admin";
		}
		map.put("error", "输入格式有误，请重新添加");
		map.putAll(adminService.getTerms());
		return "admin";

	}

	@RequestMapping("/term/{id}/{status}")
	public String termUpdate(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") Integer status) {
		System.out.println(id + "   " + status);
		Term t = adminService.getTerm(id);
		t.setStatus(status);
		adminService.save(t);
		return "redirect:/admin/term";
	}

	@GetMapping("/academy")
	public String academy(Map<String, Object> map) {
		map.putAll(adminService.getAcademies());
		return "admin";
	}

	@GetMapping("/academy/add")
	public String academyAdd(Map<String, Object> map) {
		map.put("academy", new Academy());
		map.put("operate", "add");
		return "academy";
	}

	@GetMapping("/academy/{ano}")
	public String academyUpdate(Map<String, Object> map, @PathVariable(name = "ano") String ano) {
		map.put("academy", adminService.getAcademy(ano));
		map.put("operate", "update");
		return "academy";
	}

	@PostMapping("/academy")
	public String academySave(@RequestParam(name = "academy", required = false) String[] academy,
			Map<String, Object> map, @RequestParam(name = "operate", required = false) String operate) {
		Academy a = new Academy(academy[0], academy[1], academy[2], academy[3]);
		map.putAll(adminService.validateAcademy(a));
		if (map.size() == 0) {
			if (operate.equals("add")) {
				map.putAll(adminService.exist(a));
				if (map.get("success") != null) {
					adminService.save(a);
				}
			} else if (operate.equals("update")) {
				adminService.save(a);
			}
		}
		System.out.println(a);
		map.putAll(adminService.getAcademies());
		return "admin";
	}

	@GetMapping("/course")
	public String course(Map<String, Object> map) {
		map.putAll(adminService.getCourses());
		return "admin";
	}

	@GetMapping("/course/add")
	public String courseAdd(Map<String, Object> map) {
		map.put("course", new Course());
		map.putAll(adminService.getAcademies());
		map.put("operate", "add");
		return "course";
	}

	@GetMapping("/course/{cno}")
	public String courseUpdate(Map<String, Object> map, @PathVariable(name = "cno") String cno) {
		map.put("course", adminService.getCourse(cno));
		map.putAll(adminService.getAcademies());
		map.put("operate", "update");
		return "course";
	}

	@PostMapping("/course")
	public String courseSave(@RequestParam(name = "course", required = false) String[] course, Map<String, Object> map,
			@RequestParam(name = "operate", required = false) String operate) {
		map.putAll(adminService.validateCourse(course));
		if (map.size() == 0) {
			Course c = new Course(course[0], course[1], Integer.valueOf(course[2]), Integer.valueOf(course[3]),
					course[4]);
			if (operate.equals("add")) {
				map.putAll(adminService.exist(c));
				if (map.get("success") != null) {
					adminService.save(c);
				}
			} else if (operate.equals("update")) {
				adminService.save(c);
			}
		}
		map.putAll(adminService.getCourses());
		return "admin";
	}

	@GetMapping("/student")
	public String student(Map<String, Object> map) {
		map.putAll(adminService.getStudents());
		return "admin";
	}

	@GetMapping("/student/{sno}")
	public String studentUpdatePassword(Map<String, Object> map, @PathVariable(name = "sno") String sno) {
		Student s = adminService.getStudent(sno);
		if (s.getSno() != null) {
			s.setPassword(Method.md5Encoding("12345"));
			adminService.save(s);
			map.put("success", "初始化密码成功");
		} else {
			map.put("error", "初始化密码失败");
		}
		return "forward:/admin/student";
	}

	@GetMapping("/student/add")
	public String studentAdd(Map<String, Object> map) {
		map.put("student", new Student());
		map.putAll(adminService.getAcademies());
		map.put("operate", "add");
		return "student";
	}

	@PostMapping("/student")
	public String studentSave(Map<String, Object> map, @RequestParam(name = "student") String[] student,
			@RequestParam(name = "operate") String operate) {
		map.putAll(adminService.validateStudent(student));
		if (map.get("error") == null) {
			if (operate.equals("add")) {
				Student s = (Student) map.get("student");
				System.out.println(s);
				map.putAll(adminService.exist(s));
				if (map.get("success") != null) {
					adminService.save(s);
				}
			}
		}
		map.putAll(adminService.getStudents());
		return "admin";
	}

	@GetMapping("/teacher")
	public String teacher(Map<String, Object> map) {
		map.putAll(adminService.getTeachers());
		return "admin";
	}

	@GetMapping("/teacher/{tno}")
	public String teacherUpdatePassword(Map<String, Object> map, @PathVariable(name = "tno") String tno) {
		Teacher t = adminService.getTeacher(tno);
		if (t.getTno() != null) {
			t.setPassword(Method.md5Encoding("12345"));
			adminService.save(t);
			map.put("success", "初始化密码成功");
		} else {
			map.put("error", "初始化密码失败");
		}
		return "forward:/admin/teacher";
	}

	@GetMapping("/teacher/add")
	public String teacherAdd(Map<String, Object> map) {
		map.putAll(adminService.getAcademies());
		map.put("operate", "add");
		map.put("teacher", new Teacher());
		return "teacher";
	}

	@PostMapping("/teacher")
	public String teacherSave(Map<String, Object> map, @RequestParam(name = "teacher") String[] teacher,
			@RequestParam(name = "operate") String operate) {
		map.putAll(adminService.validateTeacher(teacher));
		for(String t:teacher) {
			System.out.println(t);
		}
		if (map.get("error") == null) {
			if (operate.equals("add")) {
				Teacher t = (Teacher) map.get("teacher");
				System.out.println(t);
				map.putAll(adminService.exist(t));
				if (map.get("success") != null) {
					adminService.save(t);
				}
			}
		}
		map.putAll(adminService.getTeachers());
		return "admin";
	}

}
