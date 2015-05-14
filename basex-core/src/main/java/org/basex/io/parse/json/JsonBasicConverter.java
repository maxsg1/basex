package org.basex.io.parse.json;

import static org.basex.io.parse.json.JsonConstants.*;
import static org.basex.query.QueryError.*;
import static org.basex.util.Token.*;

import org.basex.build.json.*;
import org.basex.build.json.JsonParserOptions.JsonDuplicates;
import org.basex.query.*;
import org.basex.query.value.node.*;
import org.basex.util.list.*;

/**
 * <p>This class converts a JSON document to XML.</p>
 *
 * @author BaseX Team 2005-15, BSD License
 * @author Christian Gruen
 */
public final class JsonBasicConverter extends JsonXmlConverter {
  /** Add pairs. */
  private final BoolList addPairs = new BoolList();
  /** Unescape characters. */
  private final boolean unescape;
  /** Name of next element. */
  private byte[] name;

  /**
   * Constructor.
   * @param opts json options
   * @throws QueryIOException query I/O exception
   */
  JsonBasicConverter(final JsonParserOptions opts) throws QueryIOException {
    super(opts);
    unescape = jopts.get(JsonParserOptions.UNESCAPE);
    addPairs.add(true);
    final JsonDuplicates dupl = jopts.get(JsonParserOptions.DUPLICATES);
    if(dupl == JsonDuplicates.USE_LAST) throw new QueryIOException(
        BXJS_INVALID_X.get(null, JsonParserOptions.DUPLICATES.name(), dupl));
  }

  @Override
  void openObject() {
    open(MAP);
  }

  @Override
  void openPair(final byte[] key, final boolean add) {
    name = key;
    addPairs.add(add() && add);
  }

  @Override
  void closePair(final boolean add) {
    addPairs.pop();
  }

  @Override
  void closeObject() {
    close();
  }

  @Override
  void openArray() {
    open(ARRAY);
  }

  @Override
  void openItem() { }

  @Override
  void closeItem() { }

  @Override
  void closeArray() {
    close();
  }

  @Override
  public void numberLit(final byte[] value) {
    if(add()) addElem(NUMBER).add(value);
  }

  @Override
  public void stringLit(final byte[] value) {
    if(add()) {
      final FElem e = addElem(STRING).add(value);
      if(!unescape && contains(value, '\\')) e.add(ESCAPED, TRUE);
    }
  }

  @Override
  public void nullLit() {
    if(add()) addElem(NULL);
  }

  @Override
  public void booleanLit(final byte[] value) {
    if(add()) addElem(BOOLEAN).add(value);
  }

  /**
   * Adds a new element with the given type.
   * @param type JSON type
   * @return new element
   */
  private FElem addElem(final byte[] type) {
    final FElem e = new FElem(type, QueryText.W3_JSON_URI);
    if(name != null) {
      e.add(KEY, name);
      if(!unescape && contains(name, '\\')) e.add(ESCAPED_KEY, TRUE);
      name = null;
    }

    if(curr != null) curr.add(e);
    else curr = e;
    return e;
  }

  /**
   * Opens an entry.
   * @param type JSON type
   */
  private void open(final byte[] type) {
    if(add()) curr = addElem(type);
  }

  /**
   * Closes an entry.
   */
  private void close() {
    if(add()) {
      final FElem par = (FElem) curr.parent();
      if(par != null) curr = par;
    }
  }

  /**
   * Indicates if an entry should be added.
   * @return result of check
   */
  private boolean add() {
    return addPairs.peek();
  }
}