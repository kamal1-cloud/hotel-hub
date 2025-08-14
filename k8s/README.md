# Kubernetes Manifests

This directory contains Kubernetes deployment manifests for the Hotel Hub application.

## Structure

```
k8s/
├── base/                    # Base manifests
│   ├── deployment.yaml      # Application deployment
│   ├── service.yaml         # Service definition
│   ├── configmap.yaml       # Configuration
│   ├── secret.yaml          # Secrets template
│   └── kustomization.yaml   # Base kustomization
├── overlays/
│   ├── dev/                 # Development environment
│   │   └── kustomization.yaml
│   └── prod/                # Production environment
│       └── kustomization.yaml
└── README.md               # This file
```

## Quick Deploy

```bash
# Deploy to development
kubectl apply -k overlays/dev/

# Deploy to production  
kubectl apply -k overlays/prod/
```

For detailed deployment instructions, see [KUBERNETES_DEPLOYMENT.md](../KUBERNETES_DEPLOYMENT.md).