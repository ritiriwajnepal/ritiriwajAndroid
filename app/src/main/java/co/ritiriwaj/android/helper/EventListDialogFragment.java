package co.ritiriwaj.android.helper;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import co.ritiriwaj.android.R;
import co.ritiriwaj.android.RiwajListFragment;
import co.ritiriwaj.android.rowadapter.EventRowAdapter;
import co.ritiriwaj.android.rowadapter.RiwajRowAdapter;

public class EventListDialogFragment extends DialogFragment {

	View view;
	private List<HashMap<String, String>> eventList;
	private ListView eventListView;

	private String riwajId;

	public EventListDialogFragment(List<HashMap<String, String>> eventList,
			String riwajId) {
		this.eventList = eventList;
		this.riwajId = riwajId;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater i = getActivity().getLayoutInflater();

		view = i.inflate(R.layout.dialog_event_list, null, false);
		EventRowAdapter eventRowAdapter = new EventRowAdapter(getActivity(),
				R.layout.row_event, eventList);

		eventListView = (ListView) view.findViewById(R.id.eventListView);
		eventListView.setAdapter(eventRowAdapter);

		AlertDialog.Builder b = new AlertDialog.Builder(getActivity()).setView(
				view).setTitle("Event List");

		return b.create();

	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);

		Button eventAdd = (Button) view.findViewById(R.id.eventAddButton);
		eventAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AddEventDailogFragment addEventDailogFragment = new AddEventDailogFragment(
						getActivity(), riwajId);
				addEventDailogFragment.show(getFragmentManager(), "add event");

				getDialog().dismiss();

			}
		});

	}

}
