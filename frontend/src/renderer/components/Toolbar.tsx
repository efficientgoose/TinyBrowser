import React, { useState } from 'react';

interface ToolbarProps {
  onLoadFile: (path: string) => void;
  loading: boolean;
}

export const Toolbar: React.FC<ToolbarProps> = ({ onLoadFile, loading }) => {
  const [filePath, setFilePath] = useState('../backend/src/main/resources/sample.html');

  const handleLoad = () => {
    if (!loading && filePath.trim()) {
      onLoadFile(filePath);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleLoad();
    }
  };

  return (
    <div className="toolbar">
      <label htmlFor="file-path">File Path:</label>
      <input
        id="file-path"
        type="text"
        value={filePath}
        onChange={(e) => setFilePath(e.target.value)}
        onKeyDown={handleKeyDown}
        disabled={loading}
        placeholder="Enter HTML file path..."
      />
      <button onClick={handleLoad} disabled={loading}>
        {loading ? 'Loading...' : 'Load'}
      </button>
    </div>
  );
};
