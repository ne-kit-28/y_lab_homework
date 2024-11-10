package y_lab.audit_logging_spring_boot_starter.aspect;

import jakarta.annotation.PostConstruct;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import y_lab.audit_logging_spring_boot_starter.util.LoggingMapProperties;

/**
 * Аспект {@code LoggingAspect} отвечает за логирование
 * деталей выполнения методов приложения.
 * <p>
 * Этот аспект перехватывает вызовы методов и создает записи
 * с указанием времени выполнения и имени метода.
 */
@Aspect
//@Component
public class LoggingAspect {

    private final LoggingMapProperties properties;

    @Autowired
    public LoggingAspect(LoggingMapProperties properties) {
        this.properties = properties;
    }

//    @PostConstruct
//    public void init() {
//        PointcutParser parser = PointcutParser
//                .getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution();
//        pointcutExpression = parser.parsePointcutExpression("execution(* " + properties.getEnv() + "..*(..))");
//    }

    @Pointcut("execution(* y_lab..*(..)) && !execution(* y_lab.audit_logging_spring_boot_starter..*(..))")
    public void serviceLayerExecution() {}

    /**
     * Логирует время выполнения метода и создает запись аудита после его завершения.
     *
     * @param joinPoint информация о вызванном методе.
     * @return результат выполнения метода.
     * @throws Throwable если при выполнении метода возникает исключение.
     */
    @Around("serviceLayerExecution()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        if ( joinPoint.getTarget().getClass().getName().contains(properties.getEnv()) ) {
            long start = System.currentTimeMillis();
            Object proceed = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String methodName = methodSignature.getMethod().getName();
            String message = "Метод " + methodName + " выполнен за " + executionTime + " мс";

            System.out.println(message);
            return proceed;
        }
        return joinPoint.proceed();
    }

    /**
     * Логирует завершение метода и создает запись с возвращаемым значением.
     *
     * @param joinPoint информация о вызванном методе.
     * @param result возвращаемое значение метода.
     */
    @AfterReturning(pointcut = "serviceLayerExecution()", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {

        if (joinPoint.getTarget().getClass().getName().contains(properties.getEnv())) {
            String message = "Метод завершен: " + joinPoint.getSignature().getName()
                    + ", возвращено значение: " + result;
            System.out.println(message);
        }
    }

    /**
     * Логирует исключения, возникающие во время выполнения метода.
     *
     * @param joinPoint информация о вызванном методе.
     * @param exception исключение, возникшее при выполнении метода.
     */
    @AfterThrowing(pointcut = "serviceLayerExecution()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {

        if (joinPoint.getTarget().getClass().getName().contains(properties.getEnv())) {
            String message = "Метод: " + joinPoint.getSignature().getName()
                    + " выбросил сключение " + exception.getMessage();
            System.out.println(message);
        }
    }
}
