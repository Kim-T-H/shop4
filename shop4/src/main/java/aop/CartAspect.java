package aop;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import exception.CartEmptyException;
import exception.LoginException;
import logic.Cart;
import logic.User;


@Component
@Aspect
@Order(2)
public class CartAspect {
		@Around("execution(* controller.Cart*.check*(..))")
		public Object cartCheck(ProceedingJoinPoint joinPoint) throws Throwable{
			HttpSession session =(HttpSession)joinPoint.getArgs()[0];
			System.out.println(joinPoint);
			System.out.println(session);
			User loginUser = (User)session.getAttribute("loginUser");
			Cart cart= (Cart)session.getAttribute("CART");
			if(loginUser == null) { throw new LoginException("�ֹ��� ȸ���� �����մϴ�.�α����ϼ���","../user/login.shop"); 
			
			}if(cart==null || cart.getItemSetList().size()==0 ) { 
				throw new CartEmptyException("�ֹ��� ��ǰ�� ��ٱ��Ͽ� �����ϴ�.","../item/list.shop");
			}
			return joinPoint.proceed();
				
				
			
		}
	
}
