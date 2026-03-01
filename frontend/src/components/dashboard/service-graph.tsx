'use client'

import React, { useMemo } from 'react'
import { Service, ServiceStatus } from '@/types'
import { motion } from 'framer-motion'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
import { Share2, Activity, ShieldCheck, Database, Layout } from 'lucide-react'

interface ServiceGraphProps {
    services: Service[]
}

const ICON_MAP = {
    'Auth Service': ShieldCheck,
    'Order Service': Activity,
    'Payment Service': Database,
    'API Gateway': Share2,
    'default': Layout
}

export function ServiceGraph({ services }: ServiceGraphProps) {
    // Simple circle layout for the graph
    const nodes = useMemo(() => {
        const center = { x: 250, y: 200 }
        const radius = 150
        return services.map((service, i) => {
            const angle = (i / services.length) * 2 * Math.PI
            return {
                ...service,
                x: center.x + radius * Math.cos(angle),
                y: center.y + radius * Math.sin(angle)
            }
        })
    }, [services])

    if (services.length === 0) return null

    return (
        <Card className="border-none bg-slate-50/50 dark:bg-slate-950/20 shadow-sm overflow-hidden">
            <CardHeader className="pb-0">
                <CardTitle className="text-sm font-bold flex items-center gap-2">
                    <Share2 className="w-4 h-4 text-indigo-500" />
                    Service Dependency Graph
                </CardTitle>
            </CardHeader>
            <CardContent className="p-0 flex justify-center items-center h-[450px]">
                <svg viewBox="0 0 500 400" className="w-full h-full max-w-2xl px-8 py-4">
                    <defs>
                        <marker id="arrow" markerWidth="6" markerHeight="4" refX="15" refY="2" orient="auto">
                            <polygon points="0 0, 6 2, 0 4" className="fill-slate-300 dark:fill-slate-700" />
                        </marker>
                        <linearGradient id="activeGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                            <stop offset="0%" stopColor="#6366f1" stopOpacity="0" />
                            <stop offset="50%" stopColor="#6366f1" stopOpacity="1" />
                            <stop offset="100%" stopColor="#6366f1" stopOpacity="0" />
                        </linearGradient>
                    </defs>

                    {/* Lines between nodes */}
                    <g>
                        {nodes.map((node, i) => {
                            const nextNode = nodes[(i + 1) % nodes.length]
                            return (
                                <React.Fragment key={`line-${node.id}-${nextNode.id}`}>
                                    {/* Static Line */}
                                    <line
                                        x1={node.x} y1={node.y}
                                        x2={nextNode.x} y2={nextNode.y}
                                        className="stroke-slate-200 dark:stroke-slate-800 stroke-2"
                                        markerEnd="url(#arrow)"
                                    />
                                    {/* Traffic Animation */}
                                    <motion.circle
                                        r="2"
                                        fill="url(#activeGradient)"
                                        animate={{
                                            cx: [node.x, nextNode.x],
                                            cy: [node.y, nextNode.y],
                                            opacity: [0, 1, 0]
                                        }}
                                        transition={{
                                            duration: 2,
                                            repeat: Infinity,
                                            ease: "linear",
                                            delay: i * 0.5
                                        }}
                                    />
                                </React.Fragment>
                            )
                        })}
                    </g>

                    {/* Nodes */}
                    {nodes.map((node) => {
                        const Icon = (ICON_MAP as any)[node.name] || ICON_MAP.default
                        const statusColor = node.status === ServiceStatus.HEALTHY ? 'text-green-500' : 'text-red-500'
                        const bgColor = node.status === ServiceStatus.HEALTHY ? 'bg-green-500/10' : 'bg-red-500/10'

                        return (
                            <g key={`node-${node.id}`}>
                                {/* Node Glow */}
                                <circle
                                    cx={node.x} cy={node.y} r="25"
                                    className={`${node.status === ServiceStatus.HEALTHY ? 'fill-green-500/10' : 'fill-red-500/10'} filter blur-md`}
                                />
                                {/* Main Circle */}
                                <motion.circle
                                    cx={node.x} cy={node.y} r="20"
                                    className="fill-white dark:fill-slate-900 stroke-slate-200 dark:stroke-slate-800 stroke-1 shadow-sm"
                                    initial={{ scale: 0 }}
                                    animate={{ scale: 1 }}
                                />
                                {/* Icon Container with Tooltip/Label */}
                                <foreignObject x={node.x - 10} y={node.y - 10} width="20" height="20">
                                    <div className="flex items-center justify-center w-full h-full">
                                        <Icon className={`w-4 h-4 ${statusColor}`} />
                                    </div>
                                </foreignObject>
                                {/* Label */}
                                <text
                                    x={node.x} y={node.y + 35}
                                    textAnchor="middle"
                                    className="text-[10px] font-bold fill-slate-700 dark:fill-slate-300"
                                >
                                    {node.name}
                                </text>
                            </g>
                        )
                    })}
                </svg>
            </CardContent>
        </Card>
    )
}
