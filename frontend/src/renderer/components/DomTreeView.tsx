import React, { useState } from 'react';
import { StyledNodeData } from '../../types';

interface DomTreeViewProps {
  tree: StyledNodeData | null;
  onSelectNode: (node: StyledNodeData) => void;
}

interface TreeNodeProps {
  node: StyledNodeData;
  onSelect: (node: StyledNodeData) => void;
}

const TreeNode: React.FC<TreeNodeProps> = ({ node, onSelect }) => {
  const [expanded, setExpanded] = useState(true);

  const renderLabel = () => {
    if (node.type === 'element') {
      const attrs = Object.entries(node.attributes || {})
        .map(([k, v]) => `${k}="${v}"`)
        .join(' ');
      return `<${node.tagName}${attrs ? ' ' + attrs : ''}>`;
    }
    const text = (node.text || '').trim();
    if (text) {
      const displayText = text.length > 50 ? text.substring(0, 47) + '...' : text;
      return `TEXT: "${displayText}"`;
    }
    return 'TEXT: (whitespace)';
  };

  const hasChildren = node.children && node.children.length > 0;

  return (
    <div className="tree-node">
      <div className="tree-node-label" onClick={() => onSelect(node)}>
        {hasChildren && (
          <span
            className="tree-node-toggle"
            onClick={(e) => {
              e.stopPropagation();
              setExpanded(!expanded);
            }}
          >
            {expanded ? '▼' : '▶'}
          </span>
        )}
        <span className="tree-node-text">{renderLabel()}</span>
      </div>
      {expanded && hasChildren && (
        <div className="tree-node-children">
          {node.children.map((child, i) => (
            <TreeNode key={i} node={child} onSelect={onSelect} />
          ))}
        </div>
      )}
    </div>
  );
};

export const DomTreeView: React.FC<DomTreeViewProps> = ({ tree, onSelectNode }) => {
  if (!tree) {
    return (
      <div className="dom-tree-view">
        <div className="dom-tree-placeholder">Load an HTML file to see DOM structure</div>
      </div>
    );
  }

  return (
    <div className="dom-tree-view">
      <TreeNode node={tree} onSelect={onSelectNode} />
    </div>
  );
};
