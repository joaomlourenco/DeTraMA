package polyglot.ext.ml5.bct.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ext.ml5.bct.detection.Analyzer;
import polyglot.ext.ml5.bct.detection.Anomaly;
import polyglot.ext.ml5.bct.tree.MyNode;
import polyglot.ext.ml5.bct.tree.Visitor2AtomAccessMerger;
import polyglot.ext.ml5.bct.tree.Visitor3AtomPrinter;
import polyglot.ext.ml5.bct.tree.Visitor1AtomTracker;
import polyglot.ext.ml5.bct.tree.VisitorPrettyPrinter;
import polyglot.ext.ml5.bct.tree.VisitorSimplePrinter;

public class MethodListContext {

	protected Map<String, MyNode> map;
	protected Set<String> runners;
	
	public MethodListContext () {
		this.map = new HashMap<String, MyNode>();
		this.runners = new HashSet<String>();
	}

	public synchronized MyNode get(Object key) {
		return map.get(key);
	}

	public synchronized MyNode put(String key, MyNode value) {
		if (Utils.isStart(value.getNode()))
			runners.add(key);
		return map.put(key, value);
	}
	
	public void trace () {
		
		for (Map.Entry<String, MyNode> entry : map.entrySet()) {
			System.out.println("\n\n<<METHOD>> " + entry.getKey());
			entry.getValue().accept(new VisitorPrettyPrinter());
		}
		
		System.out.println("\n\n\n[[[ DONE PRETTY PRINTING, WILL TRACE EXECUTIONS ]]]\n\n\n");
		Map<String, MyNode> traces = new HashMap<String, MyNode>();
		for (Map.Entry<String, MyNode> entry : map.entrySet()) {
			String sig = entry.getKey();
			if (Utils.isStart(entry.getValue().getNode())) {
				Visitor1AtomTracker visitor = new Visitor1AtomTracker(sig, map);
				MyNode trace = visitor.visit();
				traces.put(sig, trace);
			}
		}
		
		Map<String, MyNode> traces2 = new HashMap<String, MyNode>();
		for (Map.Entry<String, MyNode> entry : traces.entrySet()) {
			String sig = entry.getKey();
			MyNode node = entry.getValue();
			if (node != null)
				node = node.accept(new Visitor2AtomAccessMerger());
			traces2.put(sig, node);
		}
		
		
		for (Map.Entry<String, MyNode> entry : traces2.entrySet()) {
			System.out.println("\n\n$$METHOD$$ " + entry.getKey());
			MyNode trace = entry.getValue();
			if (trace == null)
				System.out.println("trace is null!");
			else
				trace.accept(new Visitor3AtomPrinter());
				//trace.accept(new VisitorSimplePrinter());
		}
		
		System.out.println("\n\n");
		
		Analyzer analyzer = new Analyzer();
		List<Anomaly> list = new LinkedList<Anomaly>();
		for (MyNode trace1 : traces2.values())
			if (trace1 != null)
				for (MyNode trace2 : traces2.values())
					if (trace2 != null)
						list.addAll(analyzer.getAnomalies(trace1, trace2));
		for (Anomaly anomaly : list) {
			System.out.println(anomaly);
		}
	}
}
