package com.learningu.espusersim;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.tags.LinkTag;

public class IUSFilter implements NodeFilter {
    public boolean accept (Node node)
    {
        if (LinkTag.class.isAssignableFrom (node.getClass ())) {
            return true;
        } else {
        	return false;
        }
    }
}

