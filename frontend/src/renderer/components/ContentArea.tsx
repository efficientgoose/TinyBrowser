import React from 'react';

interface ContentAreaProps {
  content: string;
}

export const ContentArea: React.FC<ContentAreaProps> = ({ content }) => {
  return (
    <textarea
      className="content-area"
      value={content}
      readOnly
      placeholder="HTML content will appear here..."
    />
  );
};
