package giphyproxy;

import java.util.ArrayList;

import java.lang.UnsupportedOperationException;

public class ConnectionApprover {
	private ArrayList<ProxyTarget> approvedTargets;

	ConnectionApprover() {
		this.approvedTargets = new ArrayList<ProxyTarget>();
	}

	public void addApprovedTarget(ProxyTarget target) {
		this.approvedTargets.add(target);
	}

	public void removeTarget(ProxyTarget target) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Removing approved targets unimplemented. Destroy and recreate the object for now");
	}

	public boolean isApproved(ProxyTarget target) {

		// this.approvedTargets.contains would be nicer, but having
		// problems getting it to do a proper deep compare
		for (ProxyTarget t: this.approvedTargets) {
			if (t.equals(target)) {
				return true;
			}
		}

		return false;
	}

	public String toString() {
		return this.approvedTargets.toString();
	}
}
