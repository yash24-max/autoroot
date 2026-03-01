// Base API Response
export interface ApiResponse<T = any> {
  data: T;
  message: string;
  timestamp: string;
  success: boolean;
}

// Service Types
export interface Service {
  id: string;
  name: string;
  description?: string;
  version?: string;
  status: ServiceStatus;
  healthCheckUrl?: string;
  dependencies?: string[];
  createdAt: string;
  updatedAt: string;
}

export enum ServiceStatus {
  HEALTHY = 'HEALTHY',
  UNHEALTHY = 'UNHEALTHY',
  DEGRADED = 'DEGRADED',
  UNKNOWN = 'UNKNOWN'
}

export interface ServiceDto {
  id: string;
  name: string;
  description?: string;
  version?: string;
  status: ServiceStatus;
  healthCheckUrl?: string;
  dependencies?: string[];
}

// Incident Types
export interface Incident {
  id: string;
  serviceId: string;
  serviceName?: string;
  rootCauseService?: string;
  title: string;
  description: string;
  severity: IncidentSeverity;
  status: IncidentStatus;
  createdAt: string;
  updatedAt: string;
  resolvedAt?: string;
  assignedTo?: string;
  rootCause?: string;
  impact?: string;
}

export enum IncidentSeverity {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

export enum IncidentStatus {
  OPEN = 'OPEN',
  IN_PROGRESS = 'IN_PROGRESS',
  RESOLVED = 'RESOLVED',
  CLOSED = 'CLOSED'
}

export interface IncidentDto {
  id?: string;
  serviceId: string;
  title: string;
  description: string;
  severity: IncidentSeverity;
  status?: IncidentStatus;
  assignedTo?: string;
  rootCause?: string;
  impact?: string;
}

// Log Types
export interface LogEntry {
  id: string;
  serviceId: string;
  serviceName?: string;
  level: LogLevel;
  message: string;
  timestamp: string;
  source?: string;
  traceId?: string;
  correlationId?: string;
  metadata?: Record<string, any>;
}

export enum LogLevel {
  TRACE = 'TRACE',
  DEBUG = 'DEBUG',
  INFO = 'INFO',
  WARN = 'WARN',
  ERROR = 'ERROR',
  FATAL = 'FATAL'
}

export interface LogSearchParams {
  serviceId?: string;
  level?: LogLevel;
  startTime?: string;
  endTime?: string;
  searchTerm?: string;
  traceId?: string;
  correlationId?: string;
  page?: number;
  size?: number;
}

// Metric Types
export interface ServiceMetrics {
  serviceId: string;
  serviceName: string;
  timestamp: string;
  cpuUsage?: number;
  memoryUsage?: number;
  responseTime?: number;
  errorRate?: number;
  requestCount?: number;
  uptime?: number;
}

// Dashboard Types
export interface DashboardStats {
  totalServices: number;
  healthyServices: number;
  unhealthyServices: number;
  degradedServices: number;
  activeIncidents: number;
  criticalIncidents: number;
  totalLogs: number;
  errorLogs: number;
}

// WebSocket Message Types
export interface WebSocketMessage {
  type: WebSocketMessageType;
  data: any;
  timestamp: string;
}

export enum WebSocketMessageType {
  SERVICE_STATUS_UPDATE = 'SERVICE_STATUS_UPDATE',
  NEW_INCIDENT = 'NEW_INCIDENT',
  INCIDENT_UPDATE = 'INCIDENT_UPDATE',
  NEW_LOG_ENTRY = 'NEW_LOG_ENTRY',
  METRICS_UPDATE = 'METRICS_UPDATE',
  HEALTH_CHECK_RESULT = 'HEALTH_CHECK_RESULT'
}

// UI State Types
export interface LoadingState {
  isLoading: boolean;
  error?: string | null;
}

export interface PaginationState {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

// Filter Types
export interface ServiceFilter {
  status?: ServiceStatus[];
  search?: string;
}

export interface IncidentFilter {
  status?: IncidentStatus[];
  severity?: IncidentSeverity[];
  serviceId?: string;
  assignedTo?: string;
  dateRange?: {
    start: string;
    end: string;
  };
}