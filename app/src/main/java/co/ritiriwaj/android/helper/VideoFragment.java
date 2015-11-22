package co.ritiriwaj.android.helper;

import android.os.Bundle;
import android.util.Log;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;

public class VideoFragment extends YouTubePlayerFragment {

	public VideoFragment() {
	}

	public static VideoFragment newInstance(String url) {

		VideoFragment f = new VideoFragment();

		Bundle b = new Bundle();
		b.putString("url", url);

		f.setArguments(b);
		f.init();

		return f;
	}

	private void init() {

		initialize("AIzaSyAaUfCB6EYlpFbwcRj-Q6M7qdKxG2tmwDw",
				new OnInitializedListener() {

					@Override
					public void onInitializationFailure(Provider arg0,
							YouTubeInitializationResult arg1) {
						Log.e("YouTubeError", arg1.toString());
					}

					@Override
					public void onInitializationSuccess(
							YouTubePlayer.Provider provider,
							YouTubePlayer player, boolean wasRestored) {
						if (!wasRestored) {
							player.cueVideo(getArguments().getString("url"));
						}
					}
				});
	}
	
}
