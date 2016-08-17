package com.liferay.mobile.screens.comment.display.interactor.update;

import com.liferay.mobile.screens.base.thread.event.BasicThreadEvent;
import com.liferay.mobile.screens.models.CommentEntry;

/**
 * @author Alejandro Hernández
 */
public class CommentUpdateEvent extends BasicThreadEvent {

	public CommentUpdateEvent(CommentEntry commentEntry) {
		this.commentEntry = commentEntry;
	}

	public CommentEntry getCommentEntry() {
		return commentEntry;
	}

	private CommentEntry commentEntry;
}
