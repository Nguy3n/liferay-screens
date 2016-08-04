package com.liferay.mobile.screens.userportrait.interactor.upload;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.liferay.mobile.screens.base.MediaStoreEvent;
import com.liferay.mobile.screens.base.interactor.BaseCachedWriteRemoteInteractor;
import com.liferay.mobile.screens.cache.DefaultCachedType;
import com.liferay.mobile.screens.cache.OfflinePolicy;
import com.liferay.mobile.screens.cache.sql.CacheSQL;
import com.liferay.mobile.screens.cache.tablecache.TableCache;
import com.liferay.mobile.screens.context.LiferayScreensContext;
import com.liferay.mobile.screens.context.LiferayServerContext;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.context.User;
import com.liferay.mobile.screens.userportrait.interactor.UserPortraitInteractorListener;
import com.liferay.mobile.screens.userportrait.interactor.UserPortraitUriBuilder;
import com.liferay.mobile.screens.util.LiferayLogger;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.Iterator;
import org.json.JSONObject;

/**
 * @author Javier Gamarra
 */
public class UserPortraitUploadInteractorImpl
	extends BaseCachedWriteRemoteInteractor<UserPortraitInteractorListener, UserPortraitUploadEvent>
	implements UserPortraitUploadInteractor {

	public UserPortraitUploadInteractorImpl(int targetScreenletId, OfflinePolicy offlinePolicy) {
		super(targetScreenletId, offlinePolicy);
	}

	public void upload(final Long userId, final String picturePath) throws Exception {
		storeWithCache(userId, picturePath, false);
	}

	public void onEventMainThread(UserPortraitUploadEvent event) {

		if (!isValidEvent(event)) {
			return;
		}

		if (event.isFailed()) {
			try {
				storeToCacheAndLaunchEvent(event, event.getUserId(), event.getPicturePath());
			} catch (Exception e) {
				getListener().onUserPortraitUploadFailure(event.getException());
			}
		} else {
			if (!event.isCacheRequest()) {
				store(true, event.getUserId(), event.getPicturePath());
			}

			User oldLoggedUser = SessionContext.getCurrentUser();

			if (event.getJSONObject() != null) {
				User user = new User(event.getJSONObject());
				if (oldLoggedUser != null && user.getId() == oldLoggedUser.getId()) {
					SessionContext.setCurrentUser(user);
				}

				Uri userPortraitUri =
					new UserPortraitUriBuilder().getUserPortraitUri(LiferayServerContext.getServer(), true,
						user.getPortraitId(), user.getUuid());
				invalidateUrl(userPortraitUri);
			}

			try {
				if (oldLoggedUser != null) {
					getListener().onUserPortraitUploaded(oldLoggedUser.getId());
				}
			} catch (Exception e) {
				getListener().onUserPortraitUploadFailure(e);
			}
		}
	}

	private void invalidateUrl(Uri userPortraitURL) {
		try {
			Context context = LiferayScreensContext.getContext();

			UserPortraitUriBuilder userPortraitUriBuilder = new UserPortraitUriBuilder();
			OkHttpClient okHttpClient = userPortraitUriBuilder.getUserPortraitClient(context);

			com.squareup.okhttp.Cache cache = okHttpClient.getCache();
			Iterator<String> urls = cache.urls();
			while (urls.hasNext()) {
				String url = urls.next();
				if (url.equals(userPortraitURL.toString())) {
					urls.remove();
				}
			}

			Picasso.with(context).invalidate(userPortraitURL);
		} catch (IOException e) {
			LiferayLogger.e("Error invalidating cache", e);
		}
	}

	public void onEvent(MediaStoreEvent event) {
		if(isValidEvent(event)) {
			getListener().onPicturePathReceived(event.getFilePath());
		}
	}

	@Override
	public void online(Object[] args) throws Exception {

		long userId = (long) args[0];
		String picturePath = (String) args[1];

		Intent service = new Intent(LiferayScreensContext.getContext(), UserPortraitService.class);
		service.putExtra("picturePath", picturePath);
		service.putExtra("screenletId", getTargetScreenletId());
		service.putExtra("userId", userId);

		LiferayScreensContext.getContext().startService(service);
	}

	@Override
	protected void storeToCacheAndLaunchEvent(Object... args) {

		long userId = (long) args[0];
		String picturePath = (String) args[1];

		store(false, userId, picturePath);

		UserPortraitUploadEvent event =
			new UserPortraitUploadEvent(getTargetScreenletId(), picturePath, userId, new JSONObject());
		event.setCacheRequest(true);
		onEventMainThread(event);
	}

	private void store(boolean synced, long userId, String picturePath) {
		TableCache file = new TableCache(String.valueOf(userId), DefaultCachedType.USER_PORTRAIT_UPLOAD, picturePath);
		file.setDirty(!synced);
		CacheSQL.getInstance().set(file);
	}
}
