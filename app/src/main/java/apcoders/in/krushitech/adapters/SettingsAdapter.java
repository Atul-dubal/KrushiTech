package apcoders.in.krushitech.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.models.SettingOption;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private List<SettingOption> settings;

    public SettingsAdapter(List<SettingOption> settings) {
        this.settings = settings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingOption option = settings.get(position);
        holder.title.setText(option.getTitle());
        holder.icon.setImageResource(option.getIconResId());
        holder.itemView.setOnClickListener(v -> option.getAction().run());
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.settingTitle);
            icon = itemView.findViewById(R.id.settingIcon);
        }
    }
}
