package com.example.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.common.model.RouteInfo;
import com.example.common.routers.Router;
import com.example.common.utils.RouteMapReader;

import java.util.List;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.RouteViewHolder> {

    private Context context;
    private List<RouteInfo> routes;

    public RoutesAdapter(Context context) {
        this.context = context;
        this.routes = RouteMapReader.INSTANCE.getUniqueRoutes(context);
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        RouteInfo route = routes.get(position);
        holder.tvRouteName.setText(route.getDescription() + "\n" + route.getPath());

        holder.tvRouteName.setOnClickListener(v -> {
            // 点击跳转
            Router.switchFragment(route.getPath());
        });
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView tvRouteName;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRouteName = itemView.findViewById(R.id.tv_route_name);
        }
    }
}