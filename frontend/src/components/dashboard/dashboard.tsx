'use client'

import { useQuery } from '@tanstack/react-query'
import { servicesApi, incidentsApi } from '@/lib/api'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { LoadingSpinner } from '@/components/ui/loading-spinner'
import { ServiceStatus, IncidentSeverity, IncidentStatus } from '@/types'
import { formatDistanceToNow } from 'date-fns'
import Link from 'next/link'
import { ServiceGraph } from './service-graph'
import { useWebSocket } from '@/lib/api/use-websocket'

function getStatusColor(status: ServiceStatus | string) {
  switch (status.toUpperCase()) {
    case 'HEALTHY': return 'bg-green-500'
    case 'DEGRADED': return 'bg-yellow-500'
    case 'UNHEALTHY': return 'bg-red-500'
    default: return 'bg-gray-500'
  }
}

function getSeverityColor(severity: IncidentSeverity | string) {
  switch (severity.toUpperCase()) {
    case 'CRITICAL':
    case 'HIGH': return 'destructive'
    case 'MEDIUM': return 'secondary'
    case 'LOW': return 'outline'
    default: return 'outline'
  }
}

export function Dashboard() {
  const {
    data: servicesData,
    isLoading: servicesLoading,
    error: servicesError
  } = useQuery({
    queryKey: ['services'],
    queryFn: () => servicesApi.getAll()
  })

  const {
    data: incidentsData,
    isLoading: incidentsLoading,
    error: incidentsError
  } = useQuery({
    queryKey: ['incidents', 'open'],
    queryFn: () => incidentsApi.getOpen()
  })

  if (servicesLoading || incidentsLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <LoadingSpinner />
      </div>
    )
  }

  const services = servicesData?.data || []
  const incidents = incidentsData?.data || []

  // Stats calculation
  const stats = {
    total: services.length,
    healthy: services.filter(s => s.status === ServiceStatus.HEALTHY).length,
    degraded: services.filter(s => s.status === ServiceStatus.DEGRADED).length,
    unhealthy: services.filter(s => s.status === ServiceStatus.UNHEALTHY).length,
    openIncidents: incidents.filter(i => i.status !== IncidentStatus.RESOLVED && i.status !== IncidentStatus.CLOSED).length,
    criticalIncidents: incidents.filter(i => i.severity === IncidentSeverity.CRITICAL || i.severity === IncidentSeverity.HIGH).length,
  }

  return (
    <div className="space-y-8">
      {/* Top Section: Stats & Graph */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-1 space-y-4">
          <Card className="bg-gradient-to-br from-indigo-500/10 to-purple-500/10 border-none shadow-sm">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-bold text-indigo-700 dark:text-indigo-400">System Health</CardTitle>
            </CardHeader>
            <CardContent>
              <div className={`text-3xl font-black ${stats.unhealthy > 0 ? 'text-red-500' : 'text-green-500'}`}>
                {stats.unhealthy > 0 ? 'ACTION REQUIRED' : 'OPTIMAL'}
              </div>
              <p className="text-xs text-slate-500 mt-1 font-medium">Monitoring {stats.total} microservices</p>
            </CardContent>
          </Card>

          <div className="grid grid-cols-2 gap-4">
            <Card>
              <CardHeader className="p-4 pb-0">
                <CardTitle className="text-xs font-bold text-slate-500 uppercase">Live Incidents</CardTitle>
              </CardHeader>
              <CardContent className="p-4 pt-2">
                <div className="text-2xl font-bold">{stats.openIncidents}</div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="p-4 pb-0">
                <CardTitle className="text-xs font-bold text-slate-500 uppercase">Critical</CardTitle>
              </CardHeader>
              <CardContent className="p-4 pt-2">
                <div className="text-2xl font-bold text-red-500">{stats.criticalIncidents}</div>
              </CardContent>
            </Card>
          </div>
        </div>

        <div className="lg:col-span-2">
          <ServiceGraph services={services} />
        </div>
      </div>

      {/* Service Status Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {services.length > 0 ? services.map((service) => (
          <Card key={service.id} className="hover:shadow-lg transition-shadow">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">{service.name}</CardTitle>
              <div className={`w-3 h-3 rounded-full ${getStatusColor(service.status)}`} />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold capitalize">{service.status.toLowerCase()}</div>
              {service.updatedAt && (
                <p className="text-xs text-muted-foreground">
                  Updated {formatDistanceToNow(new Date(service.updatedAt))} ago
                </p>
              )}
            </CardContent>
          </Card>
        )) : (
          <div className="col-span-full p-8 text-center border rounded-lg bg-slate-50 dark:bg-slate-900/50">
            <p className="text-slate-500">No services detected yet.</p>
          </div>
        )}
      </div>

      {/* Recent Incidents */}
      <Card>
        <CardHeader>
          <CardTitle>Recent Incidents</CardTitle>
          <CardDescription>Latest service incidents and their status</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {incidents.length > 0 ? incidents.map((incident) => (
              <Link href={`/incidents/${incident.id}`} key={incident.id}>
                <div className="flex items-center justify-between p-4 border rounded-lg hover:bg-slate-50 dark:hover:bg-slate-900/50 transition-colors group cursor-pointer shadow-sm hover:shadow-md border-slate-200 dark:border-slate-800">
                  <div className="space-y-1">
                    <h4 className="text-sm font-bold text-slate-800 dark:text-slate-100 group-hover:text-indigo-600 dark:group-hover:text-indigo-400 transition-colors">{incident.title}</h4>
                    <div className="flex gap-2 items-center">
                      <p className="text-xs text-muted-foreground">
                        Opened {formatDistanceToNow(new Date(incident.createdAt))} ago
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center space-x-3">
                    <Badge variant={getSeverityColor(incident.severity)} className="rounded-full px-3">{incident.severity}</Badge>
                    <Badge variant="outline" className="rounded-full px-3">{incident.status}</Badge>
                  </div>
                </div>
              </Link>
            )) : (
              <div className="p-8 text-center border rounded-lg bg-slate-50 dark:bg-slate-900/50">
                <p className="text-slate-500">All systems operational. No open incidents.</p>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* Metrics Overview */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Services</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.total}</div>
            <p className="text-xs text-muted-foreground">
              {stats.healthy} healthy, {stats.degraded} degraded, {stats.unhealthy} unhealthy
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Open Incidents</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.openIncidents}</div>
            <p className="text-xs text-muted-foreground">{stats.criticalIncidents} high severity</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">System Health</CardTitle>
          </CardHeader>
          <CardContent>
            <div className={`text-2xl font-bold ${stats.unhealthy > 0 ? 'text-red-500' : 'text-green-500'}`}>
              {stats.unhealthy > 0 ? 'Action Required' : 'Optimal'}
            </div>
            <p className="text-xs text-muted-foreground">Overall platform status</p>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}