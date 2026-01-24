import React, { useState } from 'react';
import { parseHtmlFile } from '../services/api';
import { StyledNodeData } from '../types';
import { Toolbar } from './components/Toolbar';
import { DomTreeView } from './components/DomTreeView';
import { ContentArea } from './components/ContentArea';
import { StylesPanel } from './components/StylesPanel';

export const App: React.FC = () => {
  const [htmlContent, setHtmlContent] = useState('');
  const [styledTree, setStyledTree] = useState<StyledNodeData | null>(null);
  const [selectedNode, setSelectedNode] = useState<StyledNodeData | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleLoadFile = async (filePath: string) => {
    setLoading(true);
    setError(null);
    try {
      const result = await parseHtmlFile(filePath);
      setHtmlContent(result.htmlContent);
      setStyledTree(result.styledTree);
      setSelectedNode(null);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Unknown error';
      setError(errorMessage);
      console.error('Error loading file:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="browser-window">
      <Toolbar onLoadFile={handleLoadFile} loading={loading} />
      {error && <div className="error">{error}</div>}
      <div className="content-wrapper">
        <DomTreeView tree={styledTree} onSelectNode={setSelectedNode} />
        <ContentArea content={htmlContent} />
      </div>
      <StylesPanel node={selectedNode} />
    </div>
  );
};
