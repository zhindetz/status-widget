package dezz.status.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GibIntentExtra {
    private String id;
    @Nullable
    private String area;
    private String value;

    private GibIntentExtra() {}
    public static GibIntentExtra create() {
        return new GibIntentExtra();
    }

    public String getId() {
        return id;
    }

    public GibIntentExtra setId(int id) {
        this.id = id == -1 ? null : String.valueOf(id);
        return this;
    }

    @Nullable
    public String getArea() {
        return area;
    }

    public GibIntentExtra setArea(@Nullable int area) {
        this.area = area == -1 ? null : String.valueOf(area);
        return this;
    }

    public String getValue() {
        return value;
    }

    public GibIntentExtra setValue(String value) {
        this.value = value;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "GibIntentExtra{id='" + id + "', area='" + area + "', value='" + value + "'}";
    }
}
