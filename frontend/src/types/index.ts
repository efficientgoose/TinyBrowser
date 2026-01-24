export interface StyledNodeData {
  type: 'element' | 'text';
  tagName?: string;
  attributes?: Record<string, string>;
  text?: string;
  styles: Record<string, string>;
  children: StyledNodeData[];
}

export interface ParseResponse {
  htmlContent: string;
  styledTree: StyledNodeData;
}

export interface ParseRequest {
  filePath: string;
}
