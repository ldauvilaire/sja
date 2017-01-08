package ch.filecloud.samples.sja.event;

import org.springframework.context.ApplicationEvent;

public class PostPersistEvent extends ApplicationEvent {

	private static final long serialVersionUID = -8362608378581368620L;

	public PostPersistEvent(Object source) {
		super(source);
	}
}
