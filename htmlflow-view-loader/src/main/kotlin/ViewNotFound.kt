class ViewNotFound(model: Any) : RuntimeException("No view found for model type: ${model::class.simpleName}")
