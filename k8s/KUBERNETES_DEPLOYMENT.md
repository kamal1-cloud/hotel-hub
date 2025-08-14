# Kubernetes Deployment Guide - Hotel Hub

![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)
![Quarkus](https://img.shields.io/badge/Quarkus_Native-4695EB?style=for-the-badge&logo=quarkus&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)

Complete guide for deploying the Hotel Hub application to Kubernetes with native compilation for optimal performance.

## 📋 Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Build Strategies](#build-strategies)
- [Database Setup](#database-setup)
- [Application Deployment](#application-deployment)
- [Configuration Management](#configuration-management)
- [Monitoring and Observability](#monitoring-and-observability)
- [Scaling and Performance](#scaling-and-performance)
- [Troubleshooting](#troubleshooting)

## 🎯 Overview

### Deployment Options

| Deployment Type | Image Size | Startup Time | Memory Usage | Best For |
|----------------|------------|--------------|--------------|----------|
| **JVM Mode** | ~300MB | 5-10s | 200MB+ | Development, Testing |
| **Native Mode** | ~50MB | <1s | 20-60MB | Production, Auto-scaling |

### Architecture in Kubernetes

```
┌─────────────────────────────────────────────────┐
│                Kubernetes Cluster               │
├─────────────────────────────────────────────────┤
│  ┌──────────────┐    ┌────────────────────────┐ │
│  │   Ingress    │    │      ConfigMap        │ │
│  │   Controller │    │   (application.yml)   │ │
│  └──────────────┘    └────────────────────────┘ │
│           │                        │            │
│  ┌────────▼───────┐    ┌───────────▼──────────┐ │
│  │  Hotel Hub     │    │     PostgreSQL      │ │
│  │  Deployment    │◄───┤     StatefulSet     │ │
│  │  (3 replicas)  │    │   (with PVC)        │ │
│  └────────────────┘    └─────────────────────┘ │
│           │                        │            │
│  ┌────────▼───────┐    ┌───────────▼──────────┐ │
│  │    Service     │    │      Service        │ │
│  │ (LoadBalancer) │    │    (ClusterIP)      │ │
│  └────────────────┘    └─────────────────────┘ │
└─────────────────────────────────────────────────┘
```

## 🔧 Prerequisites

### Required Tools

| Tool | Version | Purpose |
|------|---------|---------|
| **kubectl** | 1.25+ | Kubernetes CLI |
| **Docker** | 20.10+ | Container building |
| **GraalVM** | 22.3+ | Native compilation (optional) |
| **Helm** | 3.10+ | Package management (optional) |

### Kubernetes Cluster Requirements

- **Kubernetes Version**: 1.25+
- **Node Resources**: Minimum 2 CPU, 4GB RAM per node
- **Storage**: Persistent volume support for PostgreSQL
- **Networking**: Ingress controller for external access

### Installation Commands

```bash
# Install kubectl (Linux)
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

# Install Helm
curl https://get.helm.sh/helm-v3.13.0-linux-amd64.tar.gz | tar -xz
sudo mv linux-amd64/helm /usr/local/bin/

# Verify cluster access
kubectl cluster-info
kubectl get nodes
```

## 🏗️ Build Strategies

### Strategy 1: Native Compilation (Recommended for Production)

#### Prerequisites for Native Build
```bash
# Install GraalVM (if not using container build)
wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.3.0/graalvm-ce-java17-linux-amd64-22.3.0.tar.gz
tar -xzf graalvm-ce-java17-linux-amd64-22.3.0.tar.gz
export GRAALVM_HOME=/path/to/graalvm-ce-java17-22.3.0
export PATH=$GRAALVM_HOME/bin:$PATH

# Install native-image
gu install native-image
```

#### Native Build Process
```bash
# 1. Build native executable
./mvnw package -Dnative -DskipTests

# 2. Build native Docker image
docker build -f src/main/docker/Dockerfile.native -t hotel-hub:native-latest .

# 3. Tag for registry
docker tag hotel-hub:native-latest your-registry.com/hotel-hub:native-1.0.0

# 4. Push to registry
docker push your-registry.com/hotel-hub:native-1.0.0
```

#### Alternative: Container-based Native Build
```bash
# Build native image without local GraalVM installation
./mvnw package -Dnative -Dquarkus.native.container-build=true -DskipTests

# Build Docker image
docker build -f src/main/docker/Dockerfile.native -t hotel-hub:native-latest .
```

### Strategy 2: JVM Mode (Development/Staging)

```bash
# 1. Build JVM application
./mvnw package -DskipTests

# 2. Build JVM Docker image
docker build -t hotel-hub:jvm-latest .

# 3. Tag and push
docker tag hotel-hub:jvm-latest your-registry.com/hotel-hub:jvm-1.0.0
docker push your-registry.com/hotel-hub:jvm-1.0.0
```

## 🗄️ Database Setup

### PostgreSQL StatefulSet

Create `k8s/postgresql-statefulset.yaml`:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: postgres-config
  namespace: hotel-hub
data:
  POSTGRES_DB: hotel_db
  POSTGRES_USER: hotel_user
---
apiVersion: v1
kind: Secret
metadata:
  name: postgres-secret
  namespace: hotel-hub
type: Opaque
data:
  # hotel_pass encoded in base64
  POSTGRES_PASSWORD: aG90ZWxfcGFzcw==
---
apiVersion: v1
kind: Service
metadata:
  name: postgresql-service
  namespace: hotel-hub
spec:
  selector:
    app: postgresql
  ports:
    - port: 5432
      targetPort: 5432
  type: ClusterIP
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgresql
  namespace: hotel-hub
spec:
  serviceName: postgresql-service
  replicas: 1
  selector:
    matchLabels:
      app: postgresql
  template:
    metadata:
      labels:
        app: postgresql
    spec:
      containers:
      - name: postgresql
        image: postgres:15-alpine
        ports:
        - containerPort: 5432
        envFrom:
        - configMapRef:
            name: postgres-config
        - secretRef:
            name: postgres-secret
        volumeMounts:
        - name: postgresql-storage
          mountPath: /var/lib/postgresql/data
        resources:
          requests:
            memory: "256Mi"
            cpu: "200m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        readinessProbe:
          exec:
            command:
              - /bin/sh
              - -c
              - pg_isready -U hotel_user -d hotel_db
          initialDelaySeconds: 15
          periodSeconds: 10
        livenessProbe:
          exec:
            command:
              - /bin/sh
              - -c
              - pg_isready -U hotel_user -d hotel_db
          initialDelaySeconds: 45
          periodSeconds: 30
  volumeClaimTemplates:
  - metadata:
      name: postgresql-storage
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 10Gi
```

## 🚀 Application Deployment

### Hotel Hub Deployment (Native)

Create `k8s/hotel-hub-deployment.yaml`:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: hotel-hub
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: hotel-hub-config
  namespace: hotel-hub
data:
  application.properties: |
    # Database configuration
    quarkus.datasource.db-kind=postgresql
    quarkus.datasource.jdbc.url=jdbc:postgresql://postgresql-service:5432/hotel_db
    quarkus.datasource.username=hotel_user
    quarkus.liquibase.migrate-at-start=true
    quarkus.liquibase.change-log=db/changelog.xml
    
    # Hibernate configuration
    quarkus.hibernate-orm.physical-naming-strategy=org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy
    
    # Cupid API client configuration
    quarkus.rest-client.cupid-api.url=https://content-api.cupid.travel
    quarkus.rest-client.cupid-api.connect-timeout=30000
    quarkus.rest-client.cupid-api.read-timeout=60000
    quarkus.rest-client.cupid-api.providers=com.hotelhub.client.CupidApiKeyFilter
    
    # Cache configuration
    quarkus.cache.caffeine."hotel-by-id".initial-capacity=100
    quarkus.cache.caffeine."hotel-by-id".maximum-size=1000
    quarkus.cache.caffeine."hotel-by-id".expire-after-write=PT30M
    
    # OpenAPI Documentation
    quarkus.smallrye-openapi.info-title=Hotel Hub API
    quarkus.smallrye-openapi.info-version=1.0.0
    quarkus.smallrye-openapi.info-description=A comprehensive hotel management API
---
apiVersion: v1
kind: Secret
metadata:
  name: hotel-hub-secret
  namespace: hotel-hub
type: Opaque
data:
  # Base64 encoded values
  POSTGRES_PASSWORD: aG90ZWxfcGFzcw==
  CUPID_API_KEY: aTJPNHA2QThzMEQzZjVHN2g5SjFrM0w1bTdOOWI=
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: hotel-hub-native
  namespace: hotel-hub
  labels:
    app: hotel-hub
    version: native
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 1
  selector:
    matchLabels:
      app: hotel-hub
  template:
    metadata:
      labels:
        app: hotel-hub
        version: native
    spec:
      containers:
      - name: hotel-hub
        image: your-registry.com/hotel-hub:native-1.0.0
        ports:
        - containerPort: 8080
          name: http
        env:
        - name: QUARKUS_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: hotel-hub-secret
              key: POSTGRES_PASSWORD
        - name: CUPID_API_KEY
          valueFrom:
            secretKeyRef:
              name: hotel-hub-secret
              key: CUPID_API_KEY
        - name: QUARKUS_HTTP_HOST
          value: "0.0.0.0"
        volumeMounts:
        - name: config-volume
          mountPath: /deployments/config
        resources:
          requests:
            memory: "32Mi"
            cpu: "100m"
          limits:
            memory: "128Mi"
            cpu: "500m"
        readinessProbe:
          httpGet:
            path: /q/health/ready
            port: 8080
          initialDelaySeconds: 1
          periodSeconds: 5
          timeoutSeconds: 3
          successThreshold: 1
          failureThreshold: 3
        livenessProbe:
          httpGet:
            path: /q/health/live
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 10
          timeoutSeconds: 3
          successThreshold: 1
          failureThreshold: 3
        startupProbe:
          httpGet:
            path: /q/health/started
            port: 8080
          initialDelaySeconds: 1
          periodSeconds: 2
          timeoutSeconds: 3
          successThreshold: 1
          failureThreshold: 30
      volumes:
      - name: config-volume
        configMap:
          name: hotel-hub-config
      restartPolicy: Always
---
apiVersion: v1
kind: Service
metadata:
  name: hotel-hub-service
  namespace: hotel-hub
spec:
  selector:
    app: hotel-hub
  ports:
    - name: http
      port: 8080
      targetPort: 8080
  type: LoadBalancer
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: hotel-hub-ingress
  namespace: hotel-hub
  annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    cert-manager.io/cluster-issuer: letsencrypt-prod
spec:
  tls:
  - hosts:
    - api.hotelhub.example.com
    secretName: hotel-hub-tls
  rules:
  - host: api.hotelhub.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: hotel-hub-service
            port:
              number: 8080
```

## ⚙️ Configuration Management

### Deployment Script

Create `k8s/deploy.sh`:

```bash
#!/bin/bash

# Hotel Hub Kubernetes Deployment Script
set -e

# Configuration
NAMESPACE="hotel-hub"
IMAGE_TAG=${1:-"native-latest"}
REGISTRY=${REGISTRY:-"your-registry.com"}

echo "🚀 Deploying Hotel Hub to Kubernetes..."
echo "Namespace: $NAMESPACE"
echo "Image: $REGISTRY/hotel-hub:$IMAGE_TAG"

# Create namespace
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

# Apply database
echo "📊 Deploying PostgreSQL..."
kubectl apply -f postgresql-statefulset.yaml

# Wait for database to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
kubectl wait --for=condition=ready pod -l app=postgresql -n $NAMESPACE --timeout=300s

# Apply application
echo "🏨 Deploying Hotel Hub Application..."
sed "s|your-registry.com/hotel-hub:native-1.0.0|$REGISTRY/hotel-hub:$IMAGE_TAG|g" hotel-hub-deployment.yaml | kubectl apply -f -

# Wait for deployment
echo "⏳ Waiting for Hotel Hub deployment to be ready..."
kubectl wait --for=condition=available deployment/hotel-hub-native -n $NAMESPACE --timeout=300s

# Show status
echo "✅ Deployment completed!"
echo ""
echo "📊 Deployment Status:"
kubectl get all -n $NAMESPACE

echo ""
echo "🌐 Access Information:"
kubectl get ingress -n $NAMESPACE

echo ""
echo "📝 Logs (last 50 lines):"
kubectl logs -n $NAMESPACE deployment/hotel-hub-native --tail=50
```

### Environment-Specific Configurations

#### Development Environment (`k8s/overlays/dev/kustomization.yaml`)
```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
- ../../base

namePrefix: dev-
namespace: hotel-hub-dev

replicas:
- name: hotel-hub-native
  count: 1

images:
- name: hotel-hub
  newTag: dev-latest

configMapGenerator:
- name: hotel-hub-config
  files:
  - application-dev.properties
  options:
    disableNameSuffixHash: true
```

#### Production Environment (`k8s/overlays/prod/kustomization.yaml`)
```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
- ../../base

namePrefix: prod-
namespace: hotel-hub-prod

replicas:
- name: hotel-hub-native
  count: 5

images:
- name: hotel-hub
  newTag: v1.0.0

configMapGenerator:
- name: hotel-hub-config
  files:
  - application-prod.properties
  options:
    disableNameSuffixHash: true

patchesStrategicMerge:
- resources-prod.yaml
```

## 📊 Monitoring and Observability

### Prometheus Monitoring

Create `k8s/monitoring/prometheus-config.yaml`:

```yaml
apiVersion: v1
kind: ServiceMonitor
metadata:
  name: hotel-hub-metrics
  namespace: hotel-hub
spec:
  selector:
    matchLabels:
      app: hotel-hub
  endpoints:
  - port: http
    path: /q/metrics
    interval: 30s
    scrapeTimeout: 10s
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: grafana-dashboard
  namespace: hotel-hub
data:
  hotel-hub-dashboard.json: |
    {
      "dashboard": {
        "title": "Hotel Hub Metrics",
        "panels": [
          {
            "title": "HTTP Requests/sec",
            "type": "graph",
            "targets": [
              {
                "expr": "rate(http_requests_total{job=\"hotel-hub\"}[5m])"
              }
            ]
          },
          {
            "title": "Memory Usage",
            "type": "graph", 
            "targets": [
              {
                "expr": "process_resident_memory_bytes{job=\"hotel-hub\"}"
              }
            ]
          }
        ]
      }
    }
```

### Logging with Fluentd

Create `k8s/monitoring/logging.yaml`:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: fluentd-config
  namespace: hotel-hub
data:
  fluent.conf: |
    <source>
      @type tail
      path /var/log/containers/hotel-hub-*.log
      pos_file /var/log/fluentd-hotel-hub.log.pos
      tag kubernetes.hotel-hub
      format json
    </source>
    
    <match kubernetes.hotel-hub>
      @type elasticsearch
      host elasticsearch-service
      port 9200
      index_name hotel-hub
    </match>
```

## ⚡ Scaling and Performance

### Horizontal Pod Autoscaler (HPA)

Create `k8s/autoscaling/hpa.yaml`:

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: hotel-hub-hpa
  namespace: hotel-hub
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: hotel-hub-native
  minReplicas: 3
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 10
        periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 60
      policies:
      - type: Percent
        value: 100
        periodSeconds: 15
      - type: Pods
        value: 4
        periodSeconds: 60
      selectPolicy: Max
```

### Vertical Pod Autoscaler (VPA)

Create `k8s/autoscaling/vpa.yaml`:

```yaml
apiVersion: autoscaling.k8s.io/v1
kind: VerticalPodAutoscaler
metadata:
  name: hotel-hub-vpa
  namespace: hotel-hub
spec:
  targetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: hotel-hub-native
  updatePolicy:
    updateMode: "Auto"
  resourcePolicy:
    containerPolicies:
    - containerName: hotel-hub
      minAllowed:
        cpu: 50m
        memory: 16Mi
      maxAllowed:
        cpu: 2
        memory: 256Mi
      controlledResources: ["cpu", "memory"]
```

## 🔍 Troubleshooting

### Common Issues and Solutions

#### 1. Pod Startup Issues

```bash
# Check pod status
kubectl get pods -n hotel-hub -o wide

# Get pod details
kubectl describe pod <pod-name> -n hotel-hub

# Check logs
kubectl logs <pod-name> -n hotel-hub -f

# Common fixes:
# - Verify image exists and is accessible
# - Check resource requests/limits
# - Verify ConfigMap and Secret references
```

#### 2. Database Connection Issues

```bash
# Test database connectivity
kubectl exec -it deployment/hotel-hub-native -n hotel-hub -- \
  /bin/sh -c "nc -zv postgresql-service 5432"

# Check PostgreSQL logs
kubectl logs statefulset/postgresql -n hotel-hub

# Verify credentials
kubectl get secret postgres-secret -n hotel-hub -o yaml
```

#### 3. Native Image Issues

```bash
# Check native image compatibility
# Ensure all dependencies support native compilation

# Add reflection configuration if needed
# Create src/main/resources/META-INF/native-image/reflect-config.json

# Build with additional debugging
./mvnw package -Dnative \
  -Dquarkus.native.additional-build-args="--verbose,--no-fallback"
```

#### 4. Performance Optimization

```yaml
# Optimize JVM settings for containers
env:
- name: JAVA_OPTIONS
  value: "-XX:MaxRAMPercentage=80.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# For native builds, adjust resource limits
resources:
  requests:
    memory: "16Mi"    # Native apps start with very low memory
    cpu: "50m"
  limits:
    memory: "64Mi"    # Can scale up as needed
    cpu: "500m"
```

### Deployment Checklist

- [ ] **Build**: Native image builds successfully
- [ ] **Registry**: Image pushed to container registry
- [ ] **Secrets**: API keys and passwords configured
- [ ] **Database**: PostgreSQL StatefulSet running
- [ ] **Network**: Services and Ingress configured
- [ ] **Monitoring**: Metrics and logging enabled
- [ ] **Scaling**: HPA and VPA configured
- [ ] **Health**: Readiness and liveness probes working
- [ ] **Performance**: Resource limits optimized

### Useful Commands

```bash
# Deploy everything
chmod +x k8s/deploy.sh
./k8s/deploy.sh native-v1.0.0

# Scale deployment
kubectl scale deployment hotel-hub-native -n hotel-hub --replicas=5

# Rolling update
kubectl set image deployment/hotel-hub-native \
  hotel-hub=your-registry.com/hotel-hub:native-v1.1.0 -n hotel-hub

# Port forward for local testing
kubectl port-forward service/hotel-hub-service -n hotel-hub 8080:8080

# Get resource usage
kubectl top pods -n hotel-hub
kubectl top nodes

# Delete deployment
kubectl delete namespace hotel-hub
```