```mermaid
flowchart LR
    Client[Client Application]

    subgraph Bus["Command Bus"]
        CB[(Command Bus)]
    end

    Handler[Command Handler]
    Domain[Domain]
    Repo[Repository]
    Store[(Event Store)]

    Client -->|Command| CB
    CB --> Handler
    Handler --> Domain
    Domain -->|Events| Repo
    Repo -->|Events| Store
    CB -->|Reply| Client