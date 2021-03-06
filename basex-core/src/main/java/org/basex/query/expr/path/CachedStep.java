package org.basex.query.expr.path;

import org.basex.query.*;
import org.basex.query.expr.*;
import org.basex.query.iter.*;
import org.basex.query.value.item.*;
import org.basex.query.value.node.*;
import org.basex.query.var.*;
import org.basex.util.*;
import org.basex.util.hash.*;

/**
 * Step expression, caching all results.
 *
 * @author BaseX Team 2005-15, BSD License
 * @author Christian Gruen
 */
final class CachedStep extends Step {
  /**
   * Constructor.
   * @param info input info
   * @param axis axis
   * @param test node test
   * @param preds predicates
   */
  CachedStep(final InputInfo info, final Axis axis, final Test test, final Expr[] preds) {
    super(info, axis, test, preds);
  }

  @Override
  public NodeSeqBuilder iter(final QueryContext qc) throws QueryException {
    // evaluate step
    final AxisIter ai = axis.iter(checkNode(qc));
    final NodeSeqBuilder nc = new NodeSeqBuilder();
    for(ANode n; (n = ai.next()) != null;) {
      if(test.eq(n)) nc.add(n.finish());
    }

    // evaluate predicates
    final boolean scoring = qc.scoring;
    for(final Expr pred : preds) {
      final long nl = nc.size();
      qc.size = nl;
      qc.pos = 1;
      int c = 0;
      for(int n = 0; n < nl; ++n) {
        final ANode node = nc.get(n);
        qc.value = node;
        final Item tst = pred.test(qc, info);
        if(tst != null) {
          // assign score value
          if(scoring) node.score(tst.score());
          nc.nodes[c++] = node;
        }
        qc.pos++;
      }
      nc.size(c);
    }
    return nc;
  }

  @Override
  public Step copy(final QueryContext qc, final VarScope scp, final IntObjMap<Var> vs) {
    final int pl = preds.length;
    final Expr[] pred = new Expr[pl];
    for(int p = 0; p < pl; p++) pred[p] = preds[p].copy(qc, scp, vs);
    return copyType(new CachedStep(info, axis, test.copy(), pred));
  }
}
