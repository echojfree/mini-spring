package com.minispring.tx.annotation;

import com.minispring.aop.Advisor;
import com.minispring.aop.Pointcut;
import com.minispring.aop.PointcutAdvisor;
import com.minispring.aop.support.AnnotationMatchingPointcut;
import com.minispring.beans.factory.BeanFactory;
import com.minispring.beans.factory.BeanFactoryAware;
import com.minispring.beans.exception.BeansException;
import com.minispring.tx.PlatformTransactionManager;
import com.minispring.tx.interceptor.TransactionInterceptor;

/**
 * TransactionAttributeSourceAdvisor - 事务属性源顾问
 * <p>
 * 为 @Transactional 注解的方法创建事务代理
 * <p>
 * 设计模式:适配器模式、策略模式
 * <p>
 * 面试要点:
 * 1. @Transactional 如何生效
 *    - TransactionAttributeSourceAdvisor 创建 Advisor
 *    - Pointcut 匹配 @Transactional 注解
 *    - TransactionInterceptor 作为 Advice
 *    - AOP 自动代理创建器创建代理
 * <p>
 * 2. Advisor 组成
 *    - Pointcut: AnnotationMatchingPointcut(@Transactional)
 *    - Advice: TransactionInterceptor
 * <p>
 * 3. 与 AOP 的集成
 *    - 注册为 Bean
 *    - DefaultAdvisorAutoProxyCreator 扫描到此 Advisor
 *    - 为匹配的 Bean 创建代理
 *
 * @author mini-spring
 */
public class TransactionAttributeSourceAdvisor implements PointcutAdvisor, BeanFactoryAware {

    /**
     * 事务管理器
     */
    private PlatformTransactionManager transactionManager;

    /**
     * 事务拦截器
     */
    private TransactionInterceptor transactionInterceptor;

    /**
     * 切点
     */
    private final Pointcut pointcut = new AnnotationMatchingPointcut(Transactional.class, true);

    public TransactionAttributeSourceAdvisor(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.transactionInterceptor = new TransactionInterceptor(transactionManager);
    }

    @Override
    public com.minispring.aop.Advice getAdvice() {
        return (com.minispring.aop.Advice) transactionInterceptor;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        // 可以从 BeanFactory 获取 TransactionManager
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.transactionInterceptor = new TransactionInterceptor(transactionManager);
    }

}
