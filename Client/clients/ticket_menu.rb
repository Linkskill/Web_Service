require 'tty-prompt'
require_relative '../modules/web_service.rb'
require_relative '../modules/validate_user_entry.rb'
require_relative '../entities/ticket_entity.rb'

# Menu com opções (listagem, pesquisa e compra) para as passagens
class TicketMenu
    def initialize
        @prompt = TTY::Prompt.new
    end

    # Exibe a tela de menu e as opções
    def display_menu
        while true
            @prompt.say("\n\t→Passagens aéreas←")
            chosen_option = @prompt.select("Escolha uma opção:", echo:false) do |menu|
                menu.enum '.'
                menu.choice 'Listar as passagens disponíveis', :show_all
                menu.choice 'Pesquisar por passagem específica', :search
                menu.choice 'Comprar uma passagem', :buy
                menu.choice 'Voltar', :back
            end

            case chosen_option
            when :show_all
                show_tickets
            when :search
                search_ticket
            when :buy
                buy_ticket
            when :back
                return
            end
        end
    end

    private
        # Exibe na tela todas as passagens do servidor
        def show_tickets
            begin
                tickets = WebService.get_tickets
            rescue => e
                @prompt.error(e.message)
                return
            end

            tickets_length = tickets.length
            if tickets_length > 0
                @prompt.say("\n#{tickets_length} passagens encontradas")
                @prompt.say("\nListando passagens:\n")

                tickets.each do |ticket|
                    print(ticket.string_converter)
                end
            else
                @prompt.say("\nNenhuma passagem foi encontrada")
            end
        end

        # Busca por passagens semelhantes de acordo com a entrada do usuário
        def search_ticket
            @prompt.say("Insira os campos a seguir para buscar a passagem desejada.")
            ticket_type = @prompt.select("Tipo da passagem: ", TicketEntity::TYPES)
            origin = @prompt.ask("Local de origem: ", convert: :string) do |e|
                e.required(true, 'Campo vazio, preencha')
            end
            destination = @prompt.ask("Local de destino: ", convert: :string) do |e|
                e.required(true, 'Campo vazio, preencha')
            end
            num_people =  @prompt.ask("Número de pessoas: ", convert: :int) do |e|
                e.required true
                e.validate(ValidateUserEntry::INT_REGEX, 'Entrada inválida, insira um número inteiro positivo')
            end
            departure_date = @prompt.ask("Data de partida(dia-mês-ano): ", convert: :date) do |e|
                e.required(true, 'Campo vazio, preencha')
                e.validate(ValidateUserEntry::DATE_REGEX, 'Insira no formato "dia-mês-ano"')
            end
            return_date =
                if ticket_type == "ida e volta"
                    @prompt.ask("Data de retorno(dia-mês-ano): ", convert: :date) do |e|
                        e.validate(ValidateUserEntry::DATE_REGEX, 'Insira no formado "dia-mês-ano"')
                    end
                else
                    nil
                end

            begin
                similar_tickets = WebService.get_similar_tickets(
                    ticket_type: ticket_type,
                    origin: origin,
                    destination: destination,
                    departure_date: departure_date,
                    return_date: return_date,
                    num_people: num_people
                )
            rescue => e
                @prompt.error(e.message)
                return
            end

            similar_tickets_length = similar_tickets.length
            if similar_tickets_length > 0
                @prompt.say("\n#{similar_tickets_length} passagens encontradas")
                
                similar_tickets.each do |ticket|
                    print(ticket.string_converter)
                end
            else
                @prompt.say("\nNenhuma passagem foi encontrada")
            end
        end

        # Compra uma passagem de acordo com a entrada do usuário
        def buy_ticket
            @prompt.say("Insira os campos a seguir para comprar uma passagem.")
            ticket_id = @prompt.ask("Identificador da passagem: ", convert: :int) do |e|
                e.required true
                e.validate(ValidateUserEntry::INT_REGEX, 'Entrada inválida, insira um número inteiro positivo')
            end

            confirmation = @prompt.yes?("Deseja comprar a passagem ##{ticket_id}?")
            if confirmation == false 
                @prompt.("Compra cancelada")
                return
            end

            begin
                response = WebService.buy_ticket(ticket_id)
                @prompt.ok(response)
            rescue => e
                @prompt.error(e.message)
            end
        end

end