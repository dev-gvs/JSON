package kz.gvsx.json;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.noties.markwon.Markwon;

class Release {
    String version;
    String changes;

    Release(String version, String changes) {
        this.version = version;
        this.changes = changes;
    }
}

class RVAdapter extends RecyclerView.Adapter<RVAdapter.ReleaseViewHolder> {

    List<Release> releaseList;
    Markwon markwon;

    RVAdapter(List<Release> releaseList, Markwon markwon) {
        this.releaseList = releaseList;
        this.markwon = markwon;
    }

    @NonNull
    @Override
    public ReleaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_row_item, viewGroup, false);
        return new ReleaseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReleaseViewHolder releaseViewHolder, int i) {
        markwon.setMarkdown(releaseViewHolder.version, releaseList.get(i).version);
        markwon.setMarkdown(releaseViewHolder.changes, releaseList.get(i).changes);
    }

    @Override
    public int getItemCount() {
        return releaseList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ReleaseViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView version;
        TextView changes;

        ReleaseViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cardView);
            version = itemView.findViewById(R.id.textViewVersionName);
            changes = itemView.findViewById(R.id.textViewVersionChanges);
        }
    }

}

public class MainActivity extends AppCompatActivity {

    private final List<Release> releaseList = new ArrayList<>();
    private RVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MySingleton singleton = MySingleton.getInstance(this.getApplicationContext());
        adapter = new RVAdapter(releaseList, singleton.getMarkwon());

        final String url = "https://api.github.com/repos/cryptomator/cryptomator/releases";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, response -> {
                    initializeData(response);
                    adapter.notifyDataSetChanged();
                }, error -> {
                });
        singleton.addToRequestQueue(jsonArrayRequest);

        LinearLayoutManager llm = new LinearLayoutManager(this.getApplicationContext());
        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
    }

    private void initializeData(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String versionName = jsonObject.getString("name");
                String versionUrl = jsonObject.getString("html_url");
                String version = "[" + versionName + "](" + versionUrl + ")";
                String changes = jsonObject.getString("body");
                releaseList.add(new Release(version, changes));
            }
        } catch (Exception ignored) {

        }
    }
}
