import React from 'react';
import { StyledNodeData } from '../../types';

interface StylesPanelProps {
  node: StyledNodeData | null;
}

export const StylesPanel: React.FC<StylesPanelProps> = ({ node }) => {
  if (!node) {
    return (
      <div className="styles-panel">
        <div className="styles-placeholder">Select a node to view computed styles</div>
      </div>
    );
  }

  const styles = Object.entries(node.styles).sort(([a], [b]) => a.localeCompare(b));

  return (
    <div className="styles-panel">
      <h3>Computed Styles:</h3>
      <div className="styles-list">
        {styles.length === 0 ? (
          <div className="no-styles">(no styles)</div>
        ) : (
          styles.map(([prop, value]) => (
            <div key={prop} className="style-entry">
              <span className="property">{prop}</span>
              <span className="colon">: </span>
              <span className="value">{value}</span>
              <span className="semicolon">;</span>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
