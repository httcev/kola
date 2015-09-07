package kola

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import grails.converters.JSON

public class AssetTypeAdapter extends TypeAdapter<Asset> {
  public Asset read(JsonReader reader) throws IOException {
    if (reader.peek() == JsonToken.NULL) {
      reader.nextNull();
      return null;
    }
    String xy = reader.nextString();
    String[] parts = xy.split(",");
    int x = Integer.parseInt(parts[0]);
    int y = Integer.parseInt(parts[1]);
    return new Asset();
  }

  public void write(JsonWriter writer, Asset value) throws IOException {
    if (value == null) {
      writer.nullValue()
      return
    }
    /*
    writer.beginObject()
    value.properties?.each { k, v ->
      println "$k -> $v"
      if (v instanceof byte[]) {
        v = v.encodeBase64().toString()
      }
      else if (v instanceof Date) {
        v = "lalalala"
      }
      writer.name(k).value(v)
    }
    writer.endObject()
    */
    writer.value((value as JSON).toString())
  }
}
