package ch.filecloud.samples.sja.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.filecloud.samples.sja.event.PostPersistEvent;

/**
 * Created by domi on 10/27/14.
 * @param <ApplicationEventPublisher>
 */
@Aspect
@Component
public class JpaLifecycleAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(JpaLifecycleAspect.class);

	@Autowired
	private ApplicationEventPublisher publisher;

	@Before("within(ch.filecloud.samples.sja..*) && @annotation(javax.persistence.PostPersist)")
	public void beforePostPersist(JoinPoint joinPoint) {
		LOGGER.info("beforePostPersist : {}", joinPoint);
		publisher.publishEvent(new PostPersistEvent(joinPoint.getTarget()));
	}

	@Around("within(ch.filecloud.samples.sja..*) && @annotation(javax.persistence.PostPersist)")
	public Object aroundPostPersist(final ProceedingJoinPoint joinPoint) {
		try {
			LOGGER.info("aroundPostPersist : Before {}", joinPoint);
			return joinPoint.proceed();
		} catch (Throwable ex) {
			LOGGER.error("aroundPostPersist : An Exception has occurred", ex);
			throw new RuntimeException(ex);
		} finally {
			LOGGER.info("aroundPostPersist : After {}", joinPoint);
			publisher.publishEvent(new PostPersistEvent(joinPoint.getTarget()));
		}
	}

	@After("within(ch.filecloud.samples.sja..*) && @annotation(javax.persistence.PostPersist)")
	public void afterPostPersist(JoinPoint joinPoint) {
		LOGGER.info("afterPostPersist : {}", joinPoint);
		publisher.publishEvent(new PostPersistEvent(joinPoint.getTarget()));
	}
}
