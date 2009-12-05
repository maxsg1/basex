package org.basex.build;

import java.io.IOException;
import org.basex.data.Data;
import org.basex.data.MemData;

/**
 * This class creates a memory based database instance.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Christian Gruen
 */
public final class MemBuilder extends Builder {
  /** Data reference. */
  private MemData data;

  /**
   * Constructor.
   * @param p parser
   */
  public MemBuilder(final Parser p) {
    super(p);
  }

  @Override
  public void init(final String db) {
    // index values are always indexed in main memory mode
    data = new MemData(tags, atts, ns, path, parser.prop);
    meta = data.meta;
    meta.name = db;
    meta.txtindex = true;
    meta.atvindex = true;
    meta.ftxindex = false;
    meta.file = parser.io;
  }

  @Override
  public void close() { }

  @Override
  protected MemData finish() throws IOException {
    data.init();
    return data;
  }

  @Override
  public void setAttValue(final int pre, final byte[] val) {
    data.text(pre, val, false);
  }

  @Override
  protected void addDoc(final byte[] txt) {
    data.insertDoc(meta.size, 0, txt);
  }

  @Override
  protected void addElem(final int dis, final int n, final int as, final int u,
      final boolean ne) {
    data.insertElem(meta.size, dis, n, as, as, u, ne);
  }

  @Override
  protected void addAttr(final int n, final byte[] v, final int d,
      final int u) {
    data.insertAttr(meta.size, d, n, v, u);
  }

  @Override
  protected void addText(final byte[] tok, final int dis, final byte kind) {
    data.insertText(meta.size, dis, tok, kind);
  }

  @Override
  protected void setSize(final int pre, final int val) {
    data.size(pre, Data.ELEM, val);
  }
}
