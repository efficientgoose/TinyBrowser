import axios from 'axios';
import { ParseResponse, ParseRequest } from '../types';

const API_BASE = 'http://localhost:8080/api';

export async function parseHtmlFile(filePath: string): Promise<ParseResponse> {
  const request: ParseRequest = { filePath };
  const response = await axios.post<ParseResponse>(`${API_BASE}/parse`, request);
  return response.data;
}
