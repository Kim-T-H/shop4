package aop;


import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import logic.User;
import exception.LoginException;

@Component	//��üȭ
@Aspect	//AOP ���� Ŭ����
@Order(1)	// AOp ���� ����
public class UserLoginAspect {
	//pointcut : controller ��Ű���� User �̸����� �����ϴ� Ŭ����.
	//			�޼����� �̸��� logincheck �� ����,  �Ű� ������ �������.
	//args(..,session)  :  �Ű������� ����� ������ �Ű������� session �� �޼���
	
	@Around	//�⺻�޼��� ���� ��, ��
	("execution(* controller.User*.loginCheck*(..)) && args(..,session)")
	public Object userLoginCheck(ProceedingJoinPoint joinPoint, HttpSession session) throws Throwable{
		User loginUser= (User)session.getAttribute("loginUser");
		if(loginUser==null) {
			throw new LoginException("[userlogin]�α��� �� �ŷ��ϼ���.","login.shop");  
		}
		return joinPoint.proceed();
	}
	
	/*
	 * AOP�����ϱ�
	 *		1.UserController�� check �� �����ϴ� �޼��忡 �Ű������� id,session �� ���
	 *			-�α��� �ȵȰ��: �α����ϼ���=> login.shop ������ �̵�
	 *			-admin�� �ƴϸ鼭, �ٸ� ���̵� ���� ��ȸ�� . ���������� ��ȸ���� => main.shop ������ �̵�.
	 */
	
	@Around
	("execution(* controller.User*.check*(..)) && args(id,session)")
	public Object myPageCheck(ProceedingJoinPoint joinPoint, String id, HttpSession session) throws Throwable{
		User loginUser= (User)session.getAttribute("loginUser");
		System.out.println("id: " + id );
		System.out.println(loginUser);
		if(loginUser==null) {
			throw new LoginException("[userlogin]�α��� �� �ŷ��ϼ���.","login.shop");  
		}else if(!loginUser.getUserid().equals("admin") && !loginUser.getUserid().equals(id))
			throw new LoginException("[userlogin] �ش� ������ �����ϴ�.","main.shop");
		return joinPoint.proceed();
	}
	
	/*
	 * ȸ�� Ż��
	 * 1.��й�ȣ ���� ����ġ: "��й�ȣ ����" �޼��� ���. delete.shop �̵�
	 * 2.��й�ȣ ���� ��ġ:	ȸ�� db����  delete
	 * 				������ ��� : logout �ϰ� login.shop ������ ��û
	 * 				�������� ���: main.shop���� ������ �̵�
	 */
	
}
