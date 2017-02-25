package smarta.smarta;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

/**
 * Created by jiangshen on 2/25/17.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CustomViewHolder> {
    private List<Trip> tripsList;
    private Context mContext;

    public RVAdapter(Context context, List<Trip> trips) {
        this.tripsList = trips;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trip_report_card, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Trip aTrip = tripsList.get(position);

        DateFormat df = new android.text.format.DateFormat();
        df.format("hh:mm:ss a", new Date());

        holder.tvTrainID.setText(String.valueOf(aTrip.getTrainID()));
        holder.tvSrcToDst.setText(aTrip.getFromSrc() + " to " + aTrip.getToDest());
        holder.tvTimeDepart.setText(df.format("hh:mm:ss a", aTrip.getStartTime()));
        holder.tvTimeArrive.setText(df.format("hh:mm:ss a", aTrip.getEndTime()));
        holder.tvDuration.setText(String.valueOf(aTrip.getDuration() / 1000) + " s");
    }

    @Override
    public int getItemCount() {
        return tripsList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tvTrainID;
        TextView tvSrcToDst;
        TextView tvTimeDepart;
        TextView tvTimeArrive;
        TextView tvDuration;

        public CustomViewHolder(View view) {
            super(view);
            tvTrainID = (TextView) view.findViewById(R.id.tv_train_id);
            tvSrcToDst = (TextView) view.findViewById(R.id.tv_src_dst);
            tvTimeDepart = (TextView) view.findViewById(R.id.tv_depart);
            tvTimeArrive = (TextView) view.findViewById(R.id.tv_arrive);
            tvDuration = (TextView) view.findViewById(R.id.tv_duration);
        }
    }
}
