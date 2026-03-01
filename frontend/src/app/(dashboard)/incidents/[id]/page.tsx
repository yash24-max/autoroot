'use client'

import React from 'react'
import { useQuery } from '@tanstack/react-query'
import { incidentsApi } from '@/lib/api'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { LoadingSpinner } from '@/components/ui/loading-spinner'
import { formatDistanceToNow } from 'date-fns'
import { useParams } from 'next/navigation'
import {
    AlertCircle,
    Activity,
    History,
    Terminal,
    ChevronRight,
    ShieldCheck,
    Zap
} from 'lucide-react'

export default function IncidentDetailPage() {
    const { id } = useParams()
    const incidentId = Array.isArray(id) ? id[0] : id

    const { data: incidentData, isLoading, error } = useQuery({
        queryKey: ['incident', incidentId],
        queryFn: () => incidentsApi.getById(incidentId as string)
    })

    if (isLoading) return <div className="flex justify-center py-20"><LoadingSpinner /></div>
    if (error || !incidentData?.success) return <div className="text-red-500 font-bold p-8 text-center bg-red-50 rounded-lg border border-red-200">Error loading incident data.</div>

    const incident = incidentData.data

    return (
        <div className="space-y-6 max-w-7xl mx-auto px-4 py-8">
            {/* Header section with glassmorphism */}
            <div className="bg-white/40 dark:bg-slate-900/40 backdrop-blur-md border border-white/20 dark:border-slate-800/40 p-6 rounded-2xl shadow-sm mb-6">
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                    <div className="space-y-1">
                        <h1 className="text-3xl font-bold text-slate-900 dark:text-white flex items-center gap-3">
                            <AlertCircle className="w-8 h-8 text-red-500" />
                            {incident.title}
                        </h1>
                        <p className="text-slate-500 dark:text-slate-400 font-medium">
                            Incident ID: {incident.id}
                        </p>
                    </div>
                    <div className="flex gap-3">
                        <Badge variant={incident.severity === 'CRITICAL' ? 'destructive' : 'secondary'} className="px-4 py-1.5 text-sm rounded-full font-bold">
                            {incident.severity}
                        </Badge>
                        <Badge variant="outline" className="px-4 py-1.5 text-sm rounded-full bg-slate-900/5 dark:bg-slate-100/5 text-slate-700 dark:text-slate-300 border-slate-200 dark:border-slate-800">
                            {incident.status}
                        </Badge>
                    </div>
                </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* Left column - AI Insights & Logs */}
                <div className="lg:col-span-2 space-y-6">
                    {/* AI Root Cause Analysis Section */}
                    <Card className="border-none bg-gradient-to-br from-indigo-500/5 to-purple-500/5 dark:from-indigo-500/10 dark:to-purple-500/10 shadow-lg shadow-indigo-500/5 relative overflow-hidden group">
                        <div className="absolute top-0 right-0 p-4 opacity-10 group-hover:scale-110 transition-transform">
                            <Zap className="w-24 h-24 text-indigo-500" />
                        </div>
                        <CardHeader className="relative">
                            <CardTitle className="flex items-center gap-2 text-indigo-700 dark:text-indigo-400">
                                <ShieldCheck className="w-5 h-5" />
                                AI Root Cause Analysis
                            </CardTitle>
                            <CardDescription className="text-indigo-600/70 dark:text-indigo-400/70">
                                LLM-generated explanation of the issue based on log patterns
                            </CardDescription>
                        </CardHeader>
                        <CardContent className="relative">
                            <p className="text-slate-700 dark:text-slate-300 leading-relaxed font-medium">
                                {incident.description || 'AI analysis is currently processing...'}
                            </p>
                            <div className="mt-4 p-4 rounded-xl bg-white/50 dark:bg-slate-900/50 border border-indigo-100 dark:border-indigo-900/30">
                                <h4 className="text-sm font-bold text-indigo-700 dark:text-indigo-300 mb-2">Recommended Actions:</h4>
                                <ul className="text-sm text-slate-600 dark:text-slate-400 list-disc list-inside space-y-1">
                                    <li>Check the database connection pool configuration.</li>
                                    <li>Verify if there was a recent deployment in the payment service.</li>
                                    <li>Increase the timeout value for external API calls temporarily.</li>
                                </ul>
                            </div>
                        </CardContent>
                    </Card>

                    {/* Log Viewer Component - Real backend data placeholder for now */}
                    <Card className="border-slate-200 dark:border-slate-800 overflow-hidden shadow-sm">
                        <CardHeader className="bg-slate-50/50 dark:bg-slate-900/50 border-b border-slate-200 dark:border-slate-800">
                            <div className="flex items-center justify-between">
                                <CardTitle className="flex items-center gap-2 text-slate-800 dark:text-slate-200 font-bold">
                                    <Terminal className="w-5 h-5 text-slate-600 dark:text-slate-400" />
                                    Correlated Logs
                                </CardTitle>
                                <div className="flex gap-2">
                                    <Badge variant="outline" className="text-[10px] px-2 py-0">Trace: {incident.id.slice(0, 8)}</Badge>
                                    <Badge variant="outline" className="text-[10px] px-2 py-0">Service: {incident.rootCauseService || 'Unknown'}</Badge>
                                </div>
                            </div>
                        </CardHeader>
                        <CardContent className="p-0">
                            <div className="bg-slate-950 p-4 font-mono text-xs text-slate-300 space-y-2 overflow-x-auto max-h-[400px] custom-scrollbar">
                                <div className="flex gap-3 hover:bg-slate-800/50 p-1 rounded transition-colors group">
                                    <span className="text-slate-500 whitespace-nowrap">2026-03-01 20:30:15</span>
                                    <span className="text-green-500 font-bold w-12 text-center border-r border-slate-700">INFO</span>
                                    <span className="text-slate-400 group-hover:text-slate-100">Received payment request for Order #129304</span>
                                </div>
                                <div className="flex gap-3 hover:bg-slate-800/50 p-1 rounded transition-colors group">
                                    <span className="text-slate-500 whitespace-nowrap">2026-03-01 20:30:18</span>
                                    <span className="text-yellow-500 font-bold w-12 text-center border-r border-slate-700">WARN</span>
                                    <span className="text-slate-400 group-hover:text-slate-100">Database connection latency: 1500ms</span>
                                </div>
                                <div className="flex gap-3 bg-red-950/30 p-1 rounded border-l-2 border-red-500 group">
                                    <span className="text-slate-500 whitespace-nowrap">2026-03-01 20:30:20</span>
                                    <span className="text-red-500 font-bold w-12 text-center border-r border-slate-700">ERROR</span>
                                    <span className="text-red-200 flex-1 group-hover:text-white">ConnectionTimeout: Could not obtain connection from HikariPool-1.</span>
                                </div>
                                <div className="flex gap-3 hover:bg-slate-800/50 p-1 rounded transition-colors group">
                                    <span className="text-slate-500 whitespace-nowrap">2026-03-01 20:30:22</span>
                                    <span className="text-red-500 font-bold w-12 text-center border-r border-slate-700">ERROR</span>
                                    <span className="text-red-200 flex-1 group-hover:text-white">Failed to process payment for transaction #tx-90283</span>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                </div>

                {/* Right column - Status & Timeline */}
                <div className="space-y-6">
                    {/* Incident Timeline Section */}
                    <Card className="border-slate-200 dark:border-slate-800 shadow-sm sticky top-8">
                        <CardHeader className="pb-3 border-b border-slate-100 dark:border-slate-900 mb-4">
                            <CardTitle className="flex items-center gap-2 text-slate-800 dark:text-slate-200 font-bold">
                                <History className="w-5 h-5 text-slate-600 dark:text-slate-400" />
                                Incident Timeline
                            </CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="space-y-6 relative">
                                {/* Vertical line connector */}
                                <div className="absolute left-[11px] top-2 bottom-2 w-0.5 bg-slate-200 dark:bg-slate-800" />

                                <div className="flex gap-4 relative">
                                    <div className="w-6 h-6 rounded-full bg-red-500 border-4 border-white dark:border-slate-900 z-10 flex items-center justify-center">
                                        <AlertCircle className="w-3 h-3 text-white" />
                                    </div>
                                    <div className="space-y-1">
                                        <p className="text-sm font-bold text-slate-900 dark:text-slate-100">Incident Detected</p>
                                        <p className="text-xs text-slate-500 font-medium">Auto-created by Correlation Engine</p>
                                        <div className="text-[10px] text-slate-400 font-bold px-1.5 py-0.5 bg-slate-100 dark:bg-slate-800 rounded inline-block">
                                            {formatDistanceToNow(new Date(incident.createdAt))} ago
                                        </div>
                                    </div>
                                </div>

                                <div className="flex gap-4 relative">
                                    <div className="w-6 h-6 rounded-full bg-indigo-500 border-4 border-white dark:border-slate-900 z-10 flex items-center justify-center">
                                        <Zap className="w-3 h-3 text-white" />
                                    </div>
                                    <div className="space-y-1">
                                        <p className="text-sm font-bold text-slate-900 dark:text-slate-100">AI Analysis Completed</p>
                                        <p className="text-xs text-slate-500 font-medium">Root cause: Database Pool Exhaustion</p>
                                        <div className="text-[10px] text-slate-400 font-bold px-1.5 py-0.5 bg-slate-100 dark:bg-slate-800 rounded inline-block">
                                            {formatDistanceToNow(new Date(incident.createdAt))} ago
                                        </div>
                                    </div>
                                </div>

                                <div className="flex gap-4 relative">
                                    <div className="w-6 h-6 rounded-full bg-yellow-500 border-4 border-white dark:border-slate-900 z-10 flex items-center justify-center">
                                        <Activity className="w-3 h-3 text-white" />
                                    </div>
                                    <div className="space-y-1">
                                        <p className="text-sm font-bold text-slate-900 dark:text-slate-100">Status Changed</p>
                                        <p className="text-xs text-slate-500 font-medium">OPEN → INVESTIGATING</p>
                                        <div className="text-[10px] text-slate-400 font-bold px-1.5 py-0.5 bg-slate-100 dark:bg-slate-800 rounded inline-block">
                                            Just now
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </CardContent>
                    </Card>

                    {/* Service Impact & Metrics Side Card */}
                    <Card className="border-slate-200 dark:border-slate-800 shadow-sm bg-slate-50/50 dark:bg-slate-950/30">
                        <CardHeader className="pb-3 border-b border-slate-100 dark:border-slate-900 mb-4">
                            <CardTitle className="text-sm font-bold text-slate-700 dark:text-slate-300">Service Impact</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="flex justify-between items-center bg-white dark:bg-slate-900 p-3 rounded-xl border border-slate-200 dark:border-slate-800">
                                <span className="text-sm font-medium text-slate-600">Error Rate</span>
                                <span className="text-sm font-bold text-red-500">+45.2%</span>
                            </div>
                            <div className="flex justify-between items-center bg-white dark:bg-slate-900 p-3 rounded-xl border border-slate-200 dark:border-slate-800">
                                <span className="text-sm font-medium text-slate-600">Avg Latency</span>
                                <span className="text-sm font-bold text-yellow-500">2.4s</span>
                            </div>
                            <div className="flex justify-between items-center bg-white dark:bg-slate-900 p-3 rounded-xl border border-slate-200 dark:border-slate-800 shadow shadow-red-500/5">
                                <span className="text-sm font-medium text-slate-600">Affected Users</span>
                                <span className="text-sm font-bold text-slate-900 dark:text-slate-100">~1,240</span>
                            </div>
                        </CardContent>
                    </Card>
                </div>
            </div>
        </div>
    )
}
